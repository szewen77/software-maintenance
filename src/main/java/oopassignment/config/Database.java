package oopassignment.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oopassignment.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal JDBC bootstrapper. Tries to open the configured DB_URL and creates tables if they don't exist.
 * Falls back silently when no JDBC driver is present so the in-memory repositories can still run.
 */
public final class Database {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);
    private static final PasswordHasher HASHER = new PasswordHasher();
    private static boolean available;

    static {
        boolean driverLoaded = true;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            LOG.warn("SQLite JDBC driver not found on classpath, falling back to in-memory.");
            driverLoaded = false;
        }

        if (driverLoaded) {
            try (Connection conn = DriverManager.getConnection(AppConfig.DB_URL)) {
                available = true;
                conn.setAutoCommit(false);
                try {
                    ensureSchemaVersion(conn);
                    createTables(conn);
                    seedData(conn);
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                available = false;
                LOG.error("DB unavailable, falling back to in-memory repositories", e);
            }
        } else {
            available = false;
        }
    }

    private Database() {
    }

    public static boolean isAvailable() {
        return available;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DB_URL);
    }

    private static void ensureSchemaVersion(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS schema_version(
                        version INTEGER PRIMARY KEY,
                        applied_at TEXT NOT NULL
                    )
                    """);
        }
        int currentVersion = getSchemaVersion(conn);
        if (currentVersion < AppConfig.SCHEMA_VERSION) {
            migrateSchema(conn, currentVersion);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR REPLACE INTO schema_version(version, applied_at) VALUES(?, datetime('now'))")) {
                ps.setInt(1, AppConfig.SCHEMA_VERSION);
                ps.executeUpdate();
            }
            LOG.info("Schema version set to {}", AppConfig.SCHEMA_VERSION);
        }
    }
    
    private static void migrateSchema(Connection conn, int fromVersion) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Migration: Change customer_id to customer_type in transaction_header
            // Check if customer_id column exists
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(transaction_header)")) {
                boolean hasCustomerId = false;
                boolean hasCustomerType = false;
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("customer_id".equals(colName)) {
                        hasCustomerId = true;
                    }
                    if ("customer_type".equals(colName)) {
                        hasCustomerType = true;
                    }
                }
                
                if (hasCustomerId && !hasCustomerType) {
                    // Migrate: Add customer_type column, populate it, then drop customer_id
                    LOG.info("Migrating transaction_header: customer_id -> customer_type");
                    stmt.executeUpdate("ALTER TABLE transaction_header ADD COLUMN customer_type TEXT");
                    // Update existing records: if member_id is not null, set to MEMBER, else WALK-IN
                    stmt.executeUpdate("""
                        UPDATE transaction_header 
                        SET customer_type = CASE 
                            WHEN member_id IS NOT NULL AND member_id != '' THEN 'MEMBER' 
                            ELSE 'WALK-IN' 
                        END
                        WHERE customer_type IS NULL
                        """);
                    // Drop the old customer_id column (SQLite doesn't support DROP COLUMN directly)
                    // We'll create a new table and copy data
                    stmt.executeUpdate("""
                        CREATE TABLE transaction_header_new(
                            transaction_id TEXT PRIMARY KEY,
                            datetime TEXT NOT NULL,
                            member_id TEXT,
                            customer_type TEXT NOT NULL,
                            total_amount REAL NOT NULL,
                            payment_method TEXT NOT NULL
                        )
                        """);
                    stmt.executeUpdate("""
                        INSERT INTO transaction_header_new 
                        SELECT transaction_id, datetime, member_id, 
                               CASE WHEN member_id IS NOT NULL AND member_id != '' THEN 'MEMBER' ELSE 'WALK-IN' END,
                               total_amount, payment_method
                        FROM transaction_header
                        """);
                    stmt.executeUpdate("DROP TABLE transaction_header");
                    stmt.executeUpdate("ALTER TABLE transaction_header_new RENAME TO transaction_header");
                    LOG.info("Migration completed: customer_id -> customer_type");
                }
            }
            
            // Drop customer table if it exists (no longer needed)
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='customer'")) {
                if (rs.next()) {
                    LOG.info("Dropping unused customer table");
                    stmt.executeUpdate("DROP TABLE customer");
                }
            }
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee(
                        id TEXT PRIMARY KEY,
                        username TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        role TEXT NOT NULL,
                        base_salary REAL NOT NULL,
                        bonus_rate REAL NOT NULL,
                        upline_id TEXT,
                        status TEXT NOT NULL
                    )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS member(
                        member_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        ic_number TEXT UNIQUE NOT NULL,
                        credit_balance REAL NOT NULL,
                        join_date TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS product(
                        product_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        price REAL NOT NULL,
                        status TEXT NOT NULL
                    )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS stock(
                        product_id TEXT NOT NULL,
                        size TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        PRIMARY KEY(product_id, size)
                    )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transaction_header(
                        transaction_id TEXT PRIMARY KEY,
                        datetime TEXT NOT NULL,
                        member_id TEXT,
                        customer_type TEXT NOT NULL,
                        total_amount REAL NOT NULL,
                        payment_method TEXT NOT NULL
                    )
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transaction_item(
                        transaction_id TEXT NOT NULL,
                        line_no INTEGER NOT NULL,
                        product_id TEXT NOT NULL,
                        size TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit_price REAL NOT NULL,
                        PRIMARY KEY(transaction_id, line_no)
                    )
                    """);
        }
    }

    private static void seedData(Connection conn) throws SQLException {
        seedEmployees(conn);
        seedMembers(conn);
        seedProducts(conn);
    }

    private static void seedEmployees(Connection conn) throws SQLException {
        int count = getCount(conn, "employee");
        if (count > 0) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            String managerHash = HASHER.hash("password123");
            String staffHash = HASHER.hash("password123");
            stmt.executeUpdate(String.format("""
                    INSERT INTO employee(id, username, password_hash, role, base_salary, bonus_rate, status)
                    VALUES ('M001','manager','%s','MANAGER',5000.0,0.20,'ACTIVE')
                    """, managerHash));
            stmt.executeUpdate(String.format("""
                    INSERT INTO employee(id, username, password_hash, role, base_salary, bonus_rate, upline_id, status)
                    VALUES ('S001','staff','%s','STAFF',3000.0,0.10,'M001','ACTIVE')
                    """, staffHash));
        }
    }

    private static void seedMembers(Connection conn) throws SQLException {
        int count = getCount(conn, "member");
        if (count > 0) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    INSERT INTO member(member_id, name, ic_number, credit_balance, join_date, status)
                    VALUES ('MB001','Alex Member','990101010101',100.0,date('now'),'ACTIVE')
                    """);
        }
    }


    private static void seedProducts(Connection conn) throws SQLException {
        int count = getCount(conn, "product");
        if (count > 0) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    INSERT INTO product(product_id,name,category,price,status)
                    VALUES ('P001','Basic Tee','Clothes',19.90,'ACTIVE')
                    """);
            stmt.executeUpdate("""
                    INSERT INTO product(product_id,name,category,price,status)
                    VALUES ('P002','Running Shoe','Shoes',120.00,'ACTIVE')
                    """);
            stmt.executeUpdate("""
                    INSERT OR REPLACE INTO stock(product_id,size,quantity) VALUES ('P001','M',10)
                    """);
            stmt.executeUpdate("""
                    INSERT OR REPLACE INTO stock(product_id,size,quantity) VALUES ('P001','L',8)
                    """);
            stmt.executeUpdate("""
                    INSERT OR REPLACE INTO stock(product_id,size,quantity) VALUES ('P002','42',5)
                    """);
        }
    }

    private static int getCount(Connection conn, String table) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM " + table)) {
            return rs.next() ? rs.getInt("c") : 0;
        }
    }

    private static int getSchemaVersion(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(version), 0) AS v FROM schema_version")) {
            return rs.next() ? rs.getInt("v") : 0;
        }
    }
}
