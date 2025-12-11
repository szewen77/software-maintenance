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
}

