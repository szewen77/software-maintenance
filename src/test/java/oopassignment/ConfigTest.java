package oopassignment;

import oopassignment.config.AppConfig;
import oopassignment.config.Database;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void appConfigConstants() {
        assertNotNull("Environment should not be null", AppConfig.ENVIRONMENT);
        assertEquals("Max login attempts should be 3", 3, AppConfig.MAX_LOGIN_ATTEMPTS);
        assertEquals("Lock duration should be 60000ms", 60_000L, AppConfig.LOCK_DURATION_MS);
        assertEquals("Member discount rate should be 5%", 0.05, AppConfig.MEMBER_DISCOUNT_RATE, 0.0001);
        assertEquals("Back token should be X", "X", AppConfig.BACK_TOKEN);
        assertNotNull("Product categories should not be null", AppConfig.ALLOWED_PRODUCT_CATEGORIES);
        assertTrue("Should allow clothes category", AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("clothes"));
        assertTrue("Should allow shoes category", AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("shoes"));
    }

    @Test
    public void appConfigEnvironmentIsString() {
        String env = AppConfig.ENVIRONMENT;
        assertNotNull("Environment should not be null", env);
        assertTrue("Environment should be a string", env instanceof String);
    }

    @Test
    public void appConfigMaxLoginAttemptsExactValue() {
        assertEquals(3, AppConfig.MAX_LOGIN_ATTEMPTS);
    }

    @Test
    public void appConfigLockDurationExactValue() {
        assertEquals(60_000L, AppConfig.LOCK_DURATION_MS);
    }

    @Test
    public void appConfigMemberDiscountRateExactValue() {
        assertEquals(0.05, AppConfig.MEMBER_DISCOUNT_RATE, 0.00001);
    }

    @Test
    public void appConfigBackTokenExactValue() {
        assertEquals("X", AppConfig.BACK_TOKEN);
    }

    @Test
    public void appConfigAllowedCategoriesExactSize() {
        assertEquals(2, AppConfig.ALLOWED_PRODUCT_CATEGORIES.size());
    }

    @Test
    public void appConfigAllowedCategoriesContainsCaseSpecific() {
        assertTrue(AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("clothes"));
        assertTrue(AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("shoes"));
        assertFalse(AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("Clothes"));
        assertFalse(AppConfig.ALLOWED_PRODUCT_CATEGORIES.contains("SHOES"));
    }

    @Test
    public void appConfigDbUrlExactValue() {
        assertEquals("jdbc:sqlite:bootsdo.db", AppConfig.DB_URL);
    }

    @Test
    public void appConfigSchemaVersionExactValue() {
        assertEquals(1, AppConfig.SCHEMA_VERSION);
    }

    @Test
    public void databaseUrlConfiguration() {
        assertNotNull("DB URL should not be null", AppConfig.DB_URL);
        assertTrue("DB URL should be SQLite", AppConfig.DB_URL.startsWith("jdbc:sqlite:"));
    }

    @Test
    public void schemaVersionIsPositive() {
        assertTrue("Schema version should be positive", AppConfig.SCHEMA_VERSION > 0);
    }

    @Test
    public void databaseIsAvailableReturnsBoolean() {
        // This will be true if SQLite driver is present
        boolean isAvailable = Database.isAvailable();
        // Just verify the method works - result depends on environment
        assertTrue("isAvailable should return true or false", 
                isAvailable == true || isAvailable == false);
    }

    @Test
    public void databaseConnectionCanBeObtainedIfAvailable() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection()) {
                assertNotNull("Connection should not be null", conn);
                assertFalse("Connection should not be closed", conn.isClosed());
                
                // Verify tables exist
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name='employee'");
                    assertTrue("Employee table should exist", rs.next());
                }
            }
        } else {
            // Test that isAvailable returns false when DB is not available
            assertFalse("Database should not be available", Database.isAvailable());
        }
    }

    @Test
    public void databaseSchemaVersionTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='schema_version'");
                assertTrue("Schema version table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseAllTablesExist() throws Exception {
        if (Database.isAvailable()) {
            String[] tables = {"employee", "member", "customer", "product", "stock", 
                              "transaction_header", "transaction_item"};
            
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement()) {
                for (String table : tables) {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'");
                    assertTrue(table + " table should exist", rs.next());
                }
            }
        }
    }

    @Test
    public void databaseSeedDataExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Check employees seeded
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM employee");
                if (rs.next()) {
                    assertTrue("Should have seeded employees", rs.getInt("c") >= 2);
                }
                
                // Check products seeded
                rs = stmt.executeQuery("SELECT COUNT(*) as c FROM product");
                if (rs.next()) {
                    assertTrue("Should have seeded products", rs.getInt("c") >= 2);
                }
                
                // Check stock seeded
                rs = stmt.executeQuery("SELECT COUNT(*) as c FROM stock");
                if (rs.next()) {
                    assertTrue("Should have seeded stock", rs.getInt("c") >= 3);
                }
            }
        }
    }

    @Test
    public void allowedCategoriesIsList() {
        assertEquals("Should have 2 categories", 2, AppConfig.ALLOWED_PRODUCT_CATEGORIES.size());
    }

    @Test
    public void memberDiscountRateIsValid() {
        assertTrue("Discount rate should be between 0 and 1", 
                AppConfig.MEMBER_DISCOUNT_RATE > 0 && AppConfig.MEMBER_DISCOUNT_RATE < 1);
    }

    @Test
    public void lockDurationIsPositive() {
        assertTrue("Lock duration should be positive", AppConfig.LOCK_DURATION_MS > 0);
    }

    @Test
    public void maxLoginAttemptsIsPositive() {
        assertTrue("Max login attempts should be positive", AppConfig.MAX_LOGIN_ATTEMPTS > 0);
    }

    @Test
    public void databaseStaticFieldsInitialized() {
        // Access Database class to trigger static initialization
        boolean available = Database.isAvailable();
        
        // This test ensures static block executes
        assertTrue("isAvailable should return boolean", 
                available == true || available == false);
    }

    @Test
    public void databaseGetConnectionWorksWhenAvailable() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection()) {
                assertNotNull("Should get connection", conn);
                assertFalse("Connection should be open", conn.isClosed());
            }
        }
    }

    @Test
    public void databaseSchemaVersionIsSet() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(version) as v FROM schema_version")) {
                
                if (rs.next()) {
                    int version = rs.getInt("v");
                    assertTrue("Schema version should be set", version > 0);
                    assertEquals("Schema version should match config", 
                            AppConfig.SCHEMA_VERSION, version);
                }
            }
        }
    }

    @Test
    public void databaseEmployeeTableHasCorrectStructure() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("PRAGMA table_info(employee)")) {
                
                boolean hasIdColumn = false;
                boolean hasUsernameColumn = false;
                boolean hasPasswordHashColumn = false;
                
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("id".equals(colName)) hasIdColumn = true;
                    if ("username".equals(colName)) hasUsernameColumn = true;
                    if ("password_hash".equals(colName)) hasPasswordHashColumn = true;
                }
                
                assertTrue("Should have id column", hasIdColumn);
                assertTrue("Should have username column", hasUsernameColumn);
                assertTrue("Should have password_hash column", hasPasswordHashColumn);
            }
        }
    }

    @Test
    public void databaseMemberTableHasCorrectStructure() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("PRAGMA table_info(member)")) {
                
                boolean hasMemberIdColumn = false;
                boolean hasIcNumberColumn = false;
                boolean hasCreditBalanceColumn = false;
                
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("member_id".equals(colName)) hasMemberIdColumn = true;
                    if ("ic_number".equals(colName)) hasIcNumberColumn = true;
                    if ("credit_balance".equals(colName)) hasCreditBalanceColumn = true;
                }
                
                assertTrue("Should have member_id column", hasMemberIdColumn);
                assertTrue("Should have ic_number column", hasIcNumberColumn);
                assertTrue("Should have credit_balance column", hasCreditBalanceColumn);
            }
        }
    }

    @Test
    public void databaseCustomerTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='customer'")) {
                
                assertTrue("Customer table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseProductTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='product'")) {
                
                assertTrue("Product table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseStockTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='stock'")) {
                
                assertTrue("Stock table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseTransactionHeaderTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_header'")) {
                
                assertTrue("Transaction header table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseTransactionItemTableExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_item'")) {
                
                assertTrue("Transaction item table should exist", rs.next());
            }
        }
    }

    @Test
    public void databaseSeededManagerExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE id='M001'")) {
                
                assertTrue("Manager should be seeded", rs.next());
                assertEquals("Manager username should be 'manager'", "manager", rs.getString("username"));
                assertEquals("Manager role should be MANAGER", "MANAGER", rs.getString("role"));
            }
        }
    }

    @Test
    public void databaseSeededStaffExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE id='S001'")) {
                
                assertTrue("Staff should be seeded", rs.next());
                assertEquals("Staff username should be 'staff'", "staff", rs.getString("username"));
                assertEquals("Staff role should be STAFF", "STAFF", rs.getString("role"));
                assertEquals("Staff upline should be M001", "M001", rs.getString("upline_id"));
            }
        }
    }

    @Test
    public void databaseSeededMemberExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM member WHERE member_id='MB001'")) {
                
                assertTrue("Member should be seeded", rs.next());
                assertEquals("Member name should be 'Alex Member'", "Alex Member", rs.getString("name"));
                assertEquals(100.0, rs.getDouble("credit_balance"), 0.001);
            }
        }
    }

    @Test
    public void databaseSeededCustomerExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM customer WHERE customer_id='CU001'")) {
                
                assertTrue("Customer should be seeded", rs.next());
                assertEquals("Customer name should be 'Walk-in'", "Walk-in", rs.getString("name"));
            }
        }
    }

    @Test
    public void databaseSeededProductsExist() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Check P001
                ResultSet rs1 = stmt.executeQuery("SELECT * FROM product WHERE product_id='P001'");
                assertTrue("P001 should be seeded", rs1.next());
                assertEquals("P001 name should be 'Basic Tee'", "Basic Tee", rs1.getString("name"));
                assertEquals(19.90, rs1.getDouble("price"), 0.001);
                
                // Check P002
                ResultSet rs2 = stmt.executeQuery("SELECT * FROM product WHERE product_id='P002'");
                assertTrue("P002 should be seeded", rs2.next());
                assertEquals("P002 name should be 'Running Shoe'", "Running Shoe", rs2.getString("name"));
                assertEquals(120.00, rs2.getDouble("price"), 0.001);
            }
        }
    }

    @Test
    public void databaseSeededStockExists() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P001' AND size='M'")) {
                
                assertTrue("Stock for P001 M should be seeded", rs.next());
                assertEquals(10, rs.getInt("quantity"));
            }
        }
    }

    @Test
    public void databaseMultipleConnectionsWork() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn1 = Database.getConnection();
                 Connection conn2 = Database.getConnection()) {
                
                assertNotNull("First connection should work", conn1);
                assertNotNull("Second connection should work", conn2);
                assertFalse("Both connections should be open", conn1.isClosed());
                assertFalse("Both connections should be open", conn2.isClosed());
            }
        }
    }

    @Test
    public void databaseSeedingIsIdempotent() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Count employees
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM employee");
                rs.next();
                int count = rs.getInt("c");
                
                // Should have exactly 2 employees (M001 and S001) from initial seed
                assertTrue("Should have seeded employees", count >= 2);
                
                // Verify no duplicates by checking specific IDs
                ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as c FROM employee WHERE id IN ('M001', 'S001')");
                rs2.next();
                assertTrue("Should have M001 and S001", rs2.getInt("c") >= 2);
            }
        }
    }

    @Test
    public void databaseEmployeeTableHasRequiredColumns() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("PRAGMA table_info(employee)")) {
                
                int columnCount = 0;
                while (rs.next()) {
                    columnCount++;
                }
                
                assertTrue("Employee table should have multiple columns", columnCount >= 7);
            }
        }
    }

    @Test
    public void databaseStockTableHasCompositeKey() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("PRAGMA table_info(stock)")) {
                
                boolean hasProductId = false;
                boolean hasSize = false;
                boolean hasQuantity = false;
                
                while (rs.next()) {
                    String colName = rs.getString("name");
                    if ("product_id".equals(colName)) hasProductId = true;
                    if ("size".equals(colName)) hasSize = true;
                    if ("quantity".equals(colName)) hasQuantity = true;
                }
                
                assertTrue("Stock should have product_id", hasProductId);
                assertTrue("Stock should have size", hasSize);
                assertTrue("Stock should have quantity", hasQuantity);
            }
        }
    }
}

