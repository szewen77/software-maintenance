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
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR REPLACE INTO schema_version(version, applied_at) VALUES(?, datetime('now'))")) {
                ps.setInt(1, AppConfig.SCHEMA_VERSION);
                ps.executeUpdate();
            }
            LOG.info("Schema version set to {}", AppConfig.SCHEMA_VERSION);
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
                    CREATE TABLE IF NOT EXISTS customer(
                        customer_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        registered_date TEXT NOT NULL,
                        last_purchase_date TEXT
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
                        customer_id TEXT,
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
        seedCustomers(conn);
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

    private static void seedCustomers(Connection conn) throws SQLException {
        int count = getCount(conn, "customer");
        if (count > 0) {
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    INSERT INTO customer(customer_id, name, registered_date)
                    VALUES ('CU001','Walk-in',date('now'))
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
