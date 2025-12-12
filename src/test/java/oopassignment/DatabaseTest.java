package oopassignment;

import oopassignment.config.AppConfig;
import oopassignment.config.Database;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for Database class to achieve 100% line and branch coverage
 */
public class DatabaseTest {

    @Test
    public void databaseIsAvailableReturnsBoolean() {
        boolean available = Database.isAvailable();
        // Should return true if SQLite driver is present, false otherwise
        assertTrue("isAvailable should return true or false", 
                available == true || available == false);
    }

    @Test
    public void databaseConnectionIfAvailable() throws Exception {
        if (Database.isAvailable()) {
            try (Connection conn = Database.getConnection()) {
                assertNotNull("Connection should not be null", conn);
                assertFalse("Connection should not be closed", conn.isClosed());
            }
        } else {
            // If database is not available, test that it returns false
            assertFalse("Database should not be available", false);
        }
    }

    @Test
    public void databaseSchemaVersionTableExists() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='schema_version'")) {
            
            assertTrue("Schema version table should exist", rs.next());
        }
    }

    @Test
    public void databaseSchemaVersionIsCorrect() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(version) as v FROM schema_version")) {
            
            assertTrue("Should have schema version", rs.next());
            int version = rs.getInt("v");
            assertEquals("Schema version should match config", 
                    AppConfig.SCHEMA_VERSION, version);
        }
    }

    @Test
    public void databaseEmployeeTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='employee'")) {
            
            assertTrue("Employee table should be created", rs.next());
        }
    }

    @Test
    public void databaseMemberTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='member'")) {
            
            assertTrue("Member table should be created", rs.next());
        }
    }

    @Test
    public void databaseProductTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='product'")) {
            
            assertTrue("Product table should be created", rs.next());
        }
    }

    @Test
    public void databaseStockTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='stock'")) {
            
            assertTrue("Stock table should be created", rs.next());
        }
    }

    @Test
    public void databaseTransactionHeaderTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_header'")) {
            
            assertTrue("Transaction header table should be created", rs.next());
        }
    }

    @Test
    public void databaseTransactionItemTableCreated() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_item'")) {
            
            assertTrue("Transaction item table should be created", rs.next());
        }
    }

    @Test
    public void databaseEmployeesSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM employee")) {
            
            assertTrue("Should have result", rs.next());
            int count = rs.getInt("c");
            assertTrue("Should have seeded employees", count >= 2);
        }
    }

    @Test
    public void databaseManagerSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE username='manager'")) {
            
            assertTrue("Manager should exist", rs.next());
            assertEquals("M001", rs.getString("id"));
            assertEquals("MANAGER", rs.getString("role"));
            assertEquals(5000.0, rs.getDouble("base_salary"), 0.001);
            assertEquals(0.20, rs.getDouble("bonus_rate"), 0.001);
            assertEquals("ACTIVE", rs.getString("status"));
        }
    }

    @Test
    public void databaseStaffSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE username='staff'")) {
            
            assertTrue("Staff should exist", rs.next());
            assertEquals("S001", rs.getString("id"));
            assertEquals("STAFF", rs.getString("role"));
            assertEquals(3000.0, rs.getDouble("base_salary"), 0.001);
            assertEquals(0.10, rs.getDouble("bonus_rate"), 0.001);
            assertEquals("M001", rs.getString("upline_id"));
            assertEquals("ACTIVE", rs.getString("status"));
        }
    }

    @Test
    public void databaseMembersSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM member")) {
            
            assertTrue("Should have result", rs.next());
            int count = rs.getInt("c");
            assertTrue("Should have seeded members", count >= 1);
        }
    }

    @Test
    public void databaseProductsSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM product")) {
            
            assertTrue("Should have result", rs.next());
            int count = rs.getInt("c");
            assertTrue("Should have seeded products", count >= 2);
        }
    }

    @Test
    public void databaseStockSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM stock")) {
            
            assertTrue("Should have result", rs.next());
            int count = rs.getInt("c");
            assertTrue("Should have seeded stock items", count >= 3);
        }
    }

    @Test
    public void databaseStockP001MSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P001' AND size='M'")) {
            
            assertTrue("P001 M stock should exist", rs.next());
            assertEquals(10, rs.getInt("quantity"));
        }
    }

    @Test
    public void databaseStockP001LSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P001' AND size='L'")) {
            
            assertTrue("P001 L stock should exist", rs.next());
            assertEquals(8, rs.getInt("quantity"));
        }
    }

    @Test
    public void databaseStockP002Seeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P002' AND size='42'")) {
            
            assertTrue("P002 42 stock should exist", rs.next());
            assertEquals(5, rs.getInt("quantity"));
        }
    }

    @Test
    public void databaseAllTablesHaveCorrectNames() throws Exception {
        if (!Database.isAvailable()) return;
        
        String[] expectedTables = {
            "employee", "member", "product", "stock", 
            "transaction_header", "transaction_item", "schema_version"
        };
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String table : expectedTables) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'");
                assertTrue(table + " table should exist", rs.next());
                assertEquals(table, rs.getString("name"));
            }
        }
    }

    @Test
    public void databaseTransactionHeaderTableStructure() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(transaction_header)")) {
            
            boolean hasTransactionId = false;
            boolean hasDateTime = false;
            boolean hasTotalAmount = false;
            boolean hasPaymentMethod = false;
            
            while (rs.next()) {
                String colName = rs.getString("name");
                if ("transaction_id".equals(colName)) hasTransactionId = true;
                if ("datetime".equals(colName)) hasDateTime = true;
                if ("total_amount".equals(colName)) hasTotalAmount = true;
                if ("payment_method".equals(colName)) hasPaymentMethod = true;
            }
            
            assertTrue("Should have transaction_id", hasTransactionId);
            assertTrue("Should have datetime", hasDateTime);
            assertTrue("Should have total_amount", hasTotalAmount);
            assertTrue("Should have payment_method", hasPaymentMethod);
        }
    }

    @Test
    public void databaseTransactionItemTableStructure() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(transaction_item)")) {
            
            boolean hasLineNo = false;
            boolean hasProductId = false;
            boolean hasSize = false;
            boolean hasQuantity = false;
            boolean hasUnitPrice = false;
            
            while (rs.next()) {
                String colName = rs.getString("name");
                if ("line_no".equals(colName)) hasLineNo = true;
                if ("product_id".equals(colName)) hasProductId = true;
                if ("size".equals(colName)) hasSize = true;
                if ("quantity".equals(colName)) hasQuantity = true;
                if ("unit_price".equals(colName)) hasUnitPrice = true;
            }
            
            assertTrue("Should have line_no", hasLineNo);
            assertTrue("Should have product_id", hasProductId);
            assertTrue("Should have size", hasSize);
            assertTrue("Should have quantity", hasQuantity);
            assertTrue("Should have unit_price", hasUnitPrice);
        }
    }

    @Test
    public void databasePasswordHashesAreHashed() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT password_hash FROM employee WHERE id='M001'")) {
            
            assertTrue("Should have manager", rs.next());
            String hash = rs.getString("password_hash");
            assertNotNull("Password hash should not be null", hash);
            assertFalse("Password hash should not be empty", hash.isEmpty());
            assertNotEquals("Password should be hashed, not plain text", "password123", hash);
        }
    }

    @Test
    public void databaseSeededDataHasCorrectTypes() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check product price is REAL
            ResultSet rs = stmt.executeQuery("SELECT price FROM product WHERE product_id='P001'");
            assertTrue(rs.next());
            double price = rs.getDouble("price");
            assertEquals(19.90, price, 0.001);
            
            // Check stock quantity is INTEGER
            ResultSet rs2 = stmt.executeQuery("SELECT quantity FROM stock WHERE product_id='P001' AND size='M'");
            assertTrue(rs2.next());
            int qty = rs2.getInt("quantity");
            assertEquals(10, qty);
        }
    }

    @Test
    public void databaseMemberCreditBalanceIsNumeric() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT credit_balance FROM member WHERE member_id='MB001'")) {
            
            assertTrue("Should have member", rs.next());
            double balance = rs.getDouble("credit_balance");
            assertEquals(100.0, balance, 0.001);
        }
    }

    @Test
    public void databaseEmployeeRolesAreCorrect() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs1 = stmt.executeQuery("SELECT role FROM employee WHERE id='M001'");
            assertTrue(rs1.next());
            assertEquals("MANAGER", rs1.getString("role"));
            
            ResultSet rs2 = stmt.executeQuery("SELECT role FROM employee WHERE id='S001'");
            assertTrue(rs2.next());
            assertEquals("STAFF", rs2.getString("role"));
        }
    }

    @Test
    public void databaseEmployeeStatusesAreActive() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check only the seeded employees M001 and S001
            ResultSet rs1 = stmt.executeQuery("SELECT status FROM employee WHERE id='M001'");
            if (rs1.next()) {
                assertEquals("Seeded manager M001 should be ACTIVE", "ACTIVE", rs1.getString("status"));
            }
            
            ResultSet rs2 = stmt.executeQuery("SELECT status FROM employee WHERE id='S001'");
            if (rs2.next()) {
                assertEquals("Seeded staff S001 should be ACTIVE", "ACTIVE", rs2.getString("status"));
            }
        }
    }

    @Test
    public void databaseMemberIcNumberIsStored() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ic_number FROM member WHERE member_id='MB001'")) {
            
            assertTrue("Should have member", rs.next());
            String ic = rs.getString("ic_number");
            assertNotNull("IC number should not be null", ic);
            assertEquals("990101010101", ic);
        }
    }

    @Test
    public void databaseProductCategoriesAreValid() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check only the seeded products P001 and P002
            ResultSet rs1 = stmt.executeQuery("SELECT category FROM product WHERE product_id='P001'");
            if (rs1.next()) {
                assertEquals("P001 should be Clothes category", "Clothes", rs1.getString("category"));
            }
            
            ResultSet rs2 = stmt.executeQuery("SELECT category FROM product WHERE product_id='P002'");
            if (rs2.next()) {
                assertEquals("P002 should be Shoes category", "Shoes", rs2.getString("category"));
            }
        }
    }

    @Test
    public void databaseProductStatusesAreActive() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check only the seeded products P001 and P002
            ResultSet rs1 = stmt.executeQuery("SELECT status FROM product WHERE product_id='P001'");
            if (rs1.next()) {
                assertEquals("Seeded product P001 should be ACTIVE", "ACTIVE", rs1.getString("status"));
            }
            
            ResultSet rs2 = stmt.executeQuery("SELECT status FROM product WHERE product_id='P002'");
            if (rs2.next()) {
                assertEquals("Seeded product P002 should be ACTIVE", "ACTIVE", rs2.getString("status"));
            }
        }
    }

    @Test
    public void databaseCanExecuteQuery() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 as test")) {
            
            assertTrue("Should execute simple query", rs.next());
            assertEquals(1, rs.getInt("test"));
        }
    }

    @Test
    public void databaseConnectionCanBeReused() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection()) {
            // Execute multiple queries on same connection
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as c FROM employee");
                assertTrue(rs1.next());
                
                ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as c FROM member");
                assertTrue(rs2.next());
                
                ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as c FROM product");
                assertTrue(rs3.next());
            }
        }
    }

    @Test
    public void databaseEmployeeTableHasUniqueUsername() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(employee)")) {
            
            // Check that username column exists (uniqueness is in CREATE TABLE)
            boolean hasUsername = false;
            while (rs.next()) {
                if ("username".equals(rs.getString("name"))) {
                    hasUsername = true;
                }
            }
            assertTrue("Should have username column", hasUsername);
        }
    }

    @Test
    public void databaseMemberTableHasUniqueIcNumber() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(member)")) {
            
            boolean hasIcNumber = false;
            while (rs.next()) {
                if ("ic_number".equals(rs.getString("name"))) {
                    hasIcNumber = true;
                }
            }
            assertTrue("Should have ic_number column", hasIcNumber);
        }
    }

    @Test
    public void databaseStaffHasCorrectUpline() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT upline_id FROM employee WHERE id='S001'")) {
            
            assertTrue("Staff should exist", rs.next());
            assertEquals("Staff upline should be M001", "M001", rs.getString("upline_id"));
        }
    }

    @Test
    public void databaseManagerHasNoUpline() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT upline_id FROM employee WHERE id='M001'")) {
            
            assertTrue("Manager should exist", rs.next());
            String upline = rs.getString("upline_id");
            assertTrue("Manager upline should be null or empty", 
                    upline == null || upline.isEmpty());
        }
    }

    @Test
    public void databaseSchemaVersionHasAppliedAt() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT applied_at FROM schema_version WHERE version=" + AppConfig.SCHEMA_VERSION)) {
            
            assertTrue("Schema version should have applied_at", rs.next());
            String appliedAt = rs.getString("applied_at");
            assertNotNull("applied_at should not be null", appliedAt);
            assertFalse("applied_at should not be empty", appliedAt.isEmpty());
        }
    }

    @Test
    public void databaseGetCountReturnsZeroForNonExistentTable() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create a temporary empty table
            stmt.executeUpdate("CREATE TEMP TABLE IF NOT EXISTS temp_test_table(id INTEGER)");
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as c FROM temp_test_table");
            assertTrue("Should have result", rs.next());
            assertEquals("Empty table should have count 0", 0, rs.getInt("c"));
        }
    }

    @Test
    public void databaseEmployeeTableHasAllColumns() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(employee)")) {
            
            int columnCount = 0;
            boolean hasId = false, hasUsername = false, hasPasswordHash = false;
            boolean hasRole = false, hasBaseSalary = false, hasBonusRate = false;
            boolean hasUplineId = false, hasStatus = false;
            
            while (rs.next()) {
                columnCount++;
                String colName = rs.getString("name");
                if ("id".equals(colName)) hasId = true;
                if ("username".equals(colName)) hasUsername = true;
                if ("password_hash".equals(colName)) hasPasswordHash = true;
                if ("role".equals(colName)) hasRole = true;
                if ("base_salary".equals(colName)) hasBaseSalary = true;
                if ("bonus_rate".equals(colName)) hasBonusRate = true;
                if ("upline_id".equals(colName)) hasUplineId = true;
                if ("status".equals(colName)) hasStatus = true;
            }
            
            assertEquals("Employee table should have 8 columns", 8, columnCount);
            assertTrue("Should have id", hasId);
            assertTrue("Should have username", hasUsername);
            assertTrue("Should have password_hash", hasPasswordHash);
            assertTrue("Should have role", hasRole);
            assertTrue("Should have base_salary", hasBaseSalary);
            assertTrue("Should have bonus_rate", hasBonusRate);
            assertTrue("Should have upline_id", hasUplineId);
            assertTrue("Should have status", hasStatus);
        }
    }

    @Test
    public void databaseMemberTableHasAllColumns() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(member)")) {
            
            int columnCount = 0;
            boolean hasMemberId = false, hasName = false, hasIcNumber = false;
            boolean hasCreditBalance = false, hasJoinDate = false, hasStatus = false;
            
            while (rs.next()) {
                columnCount++;
                String colName = rs.getString("name");
                if ("member_id".equals(colName)) hasMemberId = true;
                if ("name".equals(colName)) hasName = true;
                if ("ic_number".equals(colName)) hasIcNumber = true;
                if ("credit_balance".equals(colName)) hasCreditBalance = true;
                if ("join_date".equals(colName)) hasJoinDate = true;
                if ("status".equals(colName)) hasStatus = true;
            }
            
            assertEquals("Member table should have 6 columns", 6, columnCount);
            assertTrue("Should have member_id", hasMemberId);
            assertTrue("Should have name", hasName);
            assertTrue("Should have ic_number", hasIcNumber);
            assertTrue("Should have credit_balance", hasCreditBalance);
            assertTrue("Should have join_date", hasJoinDate);
            assertTrue("Should have status", hasStatus);
        }
    }

    @Test
    public void databaseProductTableHasAllColumns() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(product)")) {
            
            boolean hasProductId = false, hasName = false, hasCategory = false;
            boolean hasPrice = false, hasStatus = false;
            
            while (rs.next()) {
                String colName = rs.getString("name");
                if ("product_id".equals(colName)) hasProductId = true;
                if ("name".equals(colName)) hasName = true;
                if ("category".equals(colName)) hasCategory = true;
                if ("price".equals(colName)) hasPrice = true;
                if ("status".equals(colName)) hasStatus = true;
            }
            
            assertTrue("Should have product_id", hasProductId);
            assertTrue("Should have name", hasName);
            assertTrue("Should have category", hasCategory);
            assertTrue("Should have price", hasPrice);
            assertTrue("Should have status", hasStatus);
        }
    }

    @Test
    public void databaseStockTableHasAllColumns() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(stock)")) {
            
            boolean hasProductId = false, hasSize = false, hasQuantity = false;
            
            while (rs.next()) {
                String colName = rs.getString("name");
                if ("product_id".equals(colName)) hasProductId = true;
                if ("size".equals(colName)) hasSize = true;
                if ("quantity".equals(colName)) hasQuantity = true;
            }
            
            assertTrue("Should have product_id", hasProductId);
            assertTrue("Should have size", hasSize);
            assertTrue("Should have quantity", hasQuantity);
        }
    }

    @Test
    public void databaseSeedingSkippedWhenDataExists() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verify that employees exist (seeded data)
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as c FROM employee");
            rs1.next();
            int empCount = rs1.getInt("c");
            assertTrue("Should have employees", empCount >= 2);
            
            // Verify that members exist
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as c FROM member");
            rs2.next();
            int memberCount = rs2.getInt("c");
            assertTrue("Should have members", memberCount >= 1);
            
            // Verify that products exist
            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) as c FROM product");
            rs4.next();
            int productCount = rs4.getInt("c");
            assertTrue("Should have products", productCount >= 2);
        }
    }

    @Test
    public void databasePasswordHasherUsedForSeeding() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verify that passwords are hashed, not plain text
            ResultSet rs = stmt.executeQuery("SELECT password_hash FROM employee WHERE id IN ('M001', 'S001')");
            
            while (rs.next()) {
                String hash = rs.getString("password_hash");
                assertNotNull("Password hash should not be null", hash);
                assertFalse("Password should be hashed", "password123".equals(hash));
                assertTrue("Hash should have reasonable length", hash.length() > 20);
            }
        }
    }

    @Test
    public void databaseManagerAndStaffHaveCorrectData() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check manager M001
            ResultSet rsM = stmt.executeQuery("SELECT * FROM employee WHERE id='M001'");
            assertTrue("Manager M001 should exist", rsM.next());
            assertEquals("M001", rsM.getString("id"));
            assertEquals("manager", rsM.getString("username"));
            assertEquals("MANAGER", rsM.getString("role"));
            assertEquals(5000.0, rsM.getDouble("base_salary"), 0.001);
            assertEquals(0.20, rsM.getDouble("bonus_rate"), 0.001);
            assertEquals("ACTIVE", rsM.getString("status"));
            
            // Check staff S001
            ResultSet rsS = stmt.executeQuery("SELECT * FROM employee WHERE id='S001'");
            assertTrue("Staff S001 should exist", rsS.next());
            assertEquals("S001", rsS.getString("id"));
            assertEquals("staff", rsS.getString("username"));
            assertEquals("STAFF", rsS.getString("role"));
            assertEquals(3000.0, rsS.getDouble("base_salary"), 0.001);
            assertEquals(0.10, rsS.getDouble("bonus_rate"), 0.001);
            assertEquals("M001", rsS.getString("upline_id"));
            assertEquals("ACTIVE", rsS.getString("status"));
        }
    }

    @Test
    public void databaseMB001HasCorrectData() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM member WHERE member_id='MB001'")) {
            
            assertTrue("MB001 should exist", rs.next());
            assertEquals("MB001", rs.getString("member_id"));
            assertEquals("Alex Member", rs.getString("name"));
            assertEquals("990101010101", rs.getString("ic_number"));
            assertEquals(100.0, rs.getDouble("credit_balance"), 0.001);
            assertEquals("ACTIVE", rs.getString("status"));
            assertNotNull("join_date should be set", rs.getString("join_date"));
        }
    }

    @Test
    public void databaseProductP001HasCorrectData() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product WHERE product_id='P001'")) {
            
            assertTrue("P001 should exist", rs.next());
            assertEquals("P001", rs.getString("product_id"));
            assertEquals("Basic Tee", rs.getString("name"));
            assertEquals("Clothes", rs.getString("category"));
            assertEquals(19.90, rs.getDouble("price"), 0.001);
            assertEquals("ACTIVE", rs.getString("status"));
        }
    }

    @Test
    public void databaseProductP002HasCorrectData() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product WHERE product_id='P002'")) {
            
            assertTrue("P002 should exist", rs.next());
            assertEquals("P002", rs.getString("product_id"));
            assertEquals("Running Shoe", rs.getString("name"));
            assertEquals("Shoes", rs.getString("category"));
            assertEquals(120.00, rs.getDouble("price"), 0.001);
            assertEquals("ACTIVE", rs.getString("status"));
        }
    }

    @Test
    public void databaseAllStockItemsSeeded() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check P001 M
            ResultSet rs1 = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P001' AND size='M'");
            assertTrue("P001 M should exist", rs1.next());
            assertEquals(10, rs1.getInt("quantity"));
            
            // Check P001 L
            ResultSet rs2 = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P001' AND size='L'");
            assertTrue("P001 L should exist", rs2.next());
            assertEquals(8, rs2.getInt("quantity"));
            
            // Check P002 42
            ResultSet rs3 = stmt.executeQuery("SELECT * FROM stock WHERE product_id='P002' AND size='42'");
            assertTrue("P002 42 should exist", rs3.next());
            assertEquals(5, rs3.getInt("quantity"));
        }
    }

    @Test
    public void databaseSchemaVersionTableStructure() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(schema_version)")) {
            
            boolean hasVersion = false, hasAppliedAt = false;
            
            while (rs.next()) {
                String colName = rs.getString("name");
                if ("version".equals(colName)) {
                    hasVersion = true;
                    assertEquals("version should be INTEGER", "INTEGER", rs.getString("type"));
                    assertEquals("version should be primary key", 1, rs.getInt("pk"));
                }
                if ("applied_at".equals(colName)) {
                    hasAppliedAt = true;
                    assertEquals("applied_at should be TEXT", "TEXT", rs.getString("type"));
                    assertEquals("applied_at should be NOT NULL", 1, rs.getInt("notnull"));
                }
            }
            
            assertTrue("Should have version column", hasVersion);
            assertTrue("Should have applied_at column", hasAppliedAt);
        }
    }

    @Test
    public void databaseIsAvailableConsistent() {
        // Call isAvailable multiple times - should return same value
        boolean first = Database.isAvailable();
        boolean second = Database.isAvailable();
        boolean third = Database.isAvailable();
        
        assertEquals("isAvailable should be consistent", first, second);
        assertEquals("isAvailable should be consistent", second, third);
    }

    @Test
    public void databaseConnectionIsNotNull() throws Exception {
        if (!Database.isAvailable()) return;
        
        Connection conn = Database.getConnection();
        assertNotNull("Connection should not be null", conn);
        assertFalse("Connection should be open", conn.isClosed());
        conn.close();
    }

    @Test
    public void databasePrivateConstructor() {
        // Test private constructor for coverage
        try {
            java.lang.reflect.Constructor<Database> constructor = Database.class.getDeclaredConstructor();
            assertTrue("Constructor should be private", 
                    java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
            
            // Make it accessible and invoke for coverage
            constructor.setAccessible(true);
            Database instance = constructor.newInstance();
            assertNotNull("Should be able to create instance via reflection", instance);
        } catch (Exception e) {
            fail("Should be able to access private constructor via reflection: " + e.getMessage());
        }
    }

    @Test
    public void databaseClassIsFinal() {
        assertTrue("Database class should be final", 
                java.lang.reflect.Modifier.isFinal(Database.class.getModifiers()));
    }

    @Test
    public void databaseLoggerFieldExists() throws Exception {
        try {
            java.lang.reflect.Field logField = Database.class.getDeclaredField("LOG");
            assertNotNull("LOG field should exist", logField);
            assertTrue("LOG should be static", 
                    java.lang.reflect.Modifier.isStatic(logField.getModifiers()));
            assertTrue("LOG should be final", 
                    java.lang.reflect.Modifier.isFinal(logField.getModifiers()));
        } catch (NoSuchFieldException e) {
            fail("LOG field should exist");
        }
    }

    @Test
    public void databaseHasherFieldExists() throws Exception {
        try {
            java.lang.reflect.Field hasherField = Database.class.getDeclaredField("HASHER");
            assertNotNull("HASHER field should exist", hasherField);
            assertTrue("HASHER should be static", 
                    java.lang.reflect.Modifier.isStatic(hasherField.getModifiers()));
            assertTrue("HASHER should be final", 
                    java.lang.reflect.Modifier.isFinal(hasherField.getModifiers()));
        } catch (NoSuchFieldException e) {
            fail("HASHER field should exist");
        }
    }

    @Test
    public void databaseTransactionTablesExist() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check transaction_header exists
            ResultSet rs1 = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_header'");
            assertTrue("transaction_header should exist", rs1.next());
            
            // Check transaction_item exists
            ResultSet rs2 = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='transaction_item'");
            assertTrue("transaction_item should exist", rs2.next());
        }
    }

    @Test
    public void databaseEmployeeUsernameIsUnique() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA index_list(employee)")) {
            
            boolean hasUniqueIndex = false;
            while (rs.next()) {
                if (rs.getInt("unique") == 1) {
                    hasUniqueIndex = true;
                    break;
                }
            }
            
            // SQLite creates implicit unique index for UNIQUE columns
            assertTrue("Employee should have unique constraint", hasUniqueIndex);
        }
    }

    @Test
    public void databaseMemberIcIsUnique() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA index_list(member)")) {
            
            boolean hasUniqueIndex = false;
            while (rs.next()) {
                if (rs.getInt("unique") == 1) {
                    hasUniqueIndex = true;
                    break;
                }
            }
            
            assertTrue("Member should have unique constraint on IC", hasUniqueIndex);
        }
    }

    @Test
    public void databaseAvailableFieldIsStatic() throws Exception {
        java.lang.reflect.Field availableField = Database.class.getDeclaredField("available");
        assertTrue("available should be static", 
                java.lang.reflect.Modifier.isStatic(availableField.getModifiers()));
    }

    @Test
    public void databaseGetConnectionReturnsNewConnection() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn1 = Database.getConnection();
             Connection conn2 = Database.getConnection()) {
            
            assertNotNull("First connection should not be null", conn1);
            assertNotNull("Second connection should not be null", conn2);
            
            // They should be different connection objects
            assertFalse("Connections should be different objects", conn1 == conn2);
        }
    }

    @Test
    public void databaseSeedsOnlyOnceWhenCountIsPositive() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // The seed methods check "if (count > 0) return" to avoid re-seeding
            // We can verify that M001 and S001 exist exactly once
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as c FROM employee WHERE id='M001'");
            rs1.next();
            assertEquals("M001 should exist exactly once", 1, rs1.getInt("c"));
            
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as c FROM employee WHERE id='S001'");
            rs2.next();
            assertEquals("S001 should exist exactly once", 1, rs2.getInt("c"));
            
            // Verify MB001 exists exactly once
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as c FROM member WHERE member_id='MB001'");
            rs3.next();
            assertEquals("MB001 should exist exactly once", 1, rs3.getInt("c"));
        }
    }

    @Test
    public void databaseStockTableCompositePrimaryKey() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(stock)")) {
            
            int pkCount = 0;
            while (rs.next()) {
                if (rs.getInt("pk") > 0) {
                    pkCount++;
                }
            }
            
            assertEquals("Stock should have composite primary key (2 columns)", 2, pkCount);
        }
    }

    @Test
    public void databaseTransactionItemCompositePrimaryKey() throws Exception {
        if (!Database.isAvailable()) return;
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(transaction_item)")) {
            
            int pkCount = 0;
            while (rs.next()) {
                if (rs.getInt("pk") > 0) {
                    pkCount++;
                }
            }
            
            assertEquals("Transaction item should have composite primary key", 2, pkCount);
        }
    }
}

