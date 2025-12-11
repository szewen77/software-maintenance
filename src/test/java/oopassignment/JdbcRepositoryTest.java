package oopassignment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import oopassignment.config.Database;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;
import oopassignment.domain.member.CustomerRecord;
import oopassignment.domain.member.MemberRecord;
import oopassignment.domain.member.MemberStatus;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.ProductStatus;
import oopassignment.repository.impl.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for JDBC repository implementations (only run if database is available)
 */
public class JdbcRepositoryTest {

    private boolean dbAvailable;

    @Before
    public void setUp() {
        dbAvailable = Database.isAvailable();
    }

    // Employee Repository Tests
    @Test
    public void jdbcEmployeeRepositorySaveAndFind() {
        if (!dbAvailable) return;
        
        JdbcEmployeeRepository repo = new JdbcEmployeeRepository();
        EmployeeRecord emp = new EmployeeRecord("ETEST1", "testjdbc", "hash123", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        repo.save(emp);
        Optional<EmployeeRecord> found = repo.findById("ETEST1");
        
        assertTrue("Should find saved employee", found.isPresent());
        assertEquals("testjdbc", found.get().getUsername());
        
        // Cleanup
        repo.update(new EmployeeRecord("ETEST1", "testjdbc", "hash123", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.INACTIVE));
    }

    @Test
    public void jdbcEmployeeRepositoryFindByUsername() {
        if (!dbAvailable) return;
        
        JdbcEmployeeRepository repo = new JdbcEmployeeRepository();
        Optional<EmployeeRecord> found = repo.findByUsername("manager");
        
        assertTrue("Should find seeded manager", found.isPresent());
        assertEquals("M001", found.get().getId());
    }

    @Test
    public void jdbcEmployeeRepositoryFindByRole() {
        if (!dbAvailable) return;
        
        JdbcEmployeeRepository repo = new JdbcEmployeeRepository();
        List<EmployeeRecord> managers = repo.findByRole(Role.MANAGER);
        
        assertFalse("Should find managers", managers.isEmpty());
    }

    @Test
    public void jdbcEmployeeRepositoryCountByRoleAndStatus() {
        if (!dbAvailable) return;
        
        JdbcEmployeeRepository repo = new JdbcEmployeeRepository();
        long count = repo.countByRoleAndStatus(Role.MANAGER, EmploymentStatus.ACTIVE);
        
        assertTrue("Should have active managers", count >= 1);
    }

    @Test
    public void jdbcEmployeeRepositoryUpdate() {
        if (!dbAvailable) return;
        
        JdbcEmployeeRepository repo = new JdbcEmployeeRepository();
        EmployeeRecord emp = new EmployeeRecord("ETEST2", "updatetest", "hash", 
                Role.STAFF, 2000.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        repo.save(emp);
        emp.setBaseSalary(3000.0);
        repo.update(emp);
        
        EmployeeRecord updated = repo.findById("ETEST2").orElseThrow();
        assertEquals(3000.0, updated.getBaseSalary(), 0.001);
    }

    // Member Repository Tests
    @Test
    public void jdbcMemberRepositorySaveAndFind() {
        if (!dbAvailable) return;
        
        JdbcMemberRepository repo = new JdbcMemberRepository();
        MemberRecord member = new MemberRecord("MBTEST1", "Test JDBC Member", "123456789012",
                50.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        Optional<MemberRecord> found = repo.findById("MBTEST1");
        
        assertTrue("Should find saved member", found.isPresent());
        assertEquals("Test JDBC Member", found.get().getName());
    }

    @Test
    public void jdbcMemberRepositoryFindByIc() {
        if (!dbAvailable) return;
        
        JdbcMemberRepository repo = new JdbcMemberRepository();
        MemberRecord member = new MemberRecord("MBTEST2", "IC Test", "210987654321",
                100.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        Optional<MemberRecord> found = repo.findByIc("210987654321");
        
        assertTrue("Should find by IC", found.isPresent());
        assertEquals("MBTEST2", found.get().getMemberId());
    }

    @Test
    public void jdbcMemberRepositorySearchByName() {
        if (!dbAvailable) return;
        
        JdbcMemberRepository repo = new JdbcMemberRepository();
        List<MemberRecord> results = repo.searchByName("Member");
        
        assertNotNull("Should return list", results);
    }

    @Test
    public void jdbcMemberRepositoryUpdate() {
        if (!dbAvailable) return;
        
        JdbcMemberRepository repo = new JdbcMemberRepository();
        MemberRecord member = new MemberRecord("MBTEST3", "Update Test", "345678901234",
                50.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        member.setCreditBalance(150.0);
        repo.update(member);
        
        MemberRecord updated = repo.findById("MBTEST3").orElseThrow();
        assertEquals(150.0, updated.getCreditBalance(), 0.001);
    }

    // Customer Repository Tests
    @Test
    public void jdbcCustomerRepositorySaveAndFind() {
        if (!dbAvailable) return;
        
        JdbcCustomerRepository repo = new JdbcCustomerRepository();
        CustomerRecord customer = new CustomerRecord("CUTEST1", "Test JDBC Customer",
                LocalDate.now(), null);
        
        repo.save(customer);
        Optional<CustomerRecord> found = repo.findById("CUTEST1");
        
        assertTrue("Should find saved customer", found.isPresent());
        assertEquals("Test JDBC Customer", found.get().getName());
    }

    @Test
    public void jdbcCustomerRepositoryUpdate() {
        if (!dbAvailable) return;
        
        JdbcCustomerRepository repo = new JdbcCustomerRepository();
        CustomerRecord customer = new CustomerRecord("CUTEST2", "Update Customer",
                LocalDate.now(), null);
        
        repo.save(customer);
        customer.setLastPurchaseDate(LocalDate.now());
        repo.update(customer);
        
        CustomerRecord updated = repo.findById("CUTEST2").orElseThrow();
        assertNotNull("Last purchase date should be set", updated.getLastPurchaseDate());
    }

    // Product Repository Tests
    @Test
    public void jdbcProductRepositorySaveAndFind() {
        if (!dbAvailable) return;
        
        JdbcProductRepository repo = new JdbcProductRepository();
        ProductRecord product = new ProductRecord("PTEST1", "Test JDBC Product", "clothes",
                99.90, ProductStatus.ACTIVE);
        
        repo.save(product);
        Optional<ProductRecord> found = repo.findById("PTEST1");
        
        assertTrue("Should find saved product", found.isPresent());
        assertEquals("Test JDBC Product", found.get().getName());
    }

    @Test
    public void jdbcProductRepositoryUpdate() {
        if (!dbAvailable) return;
        
        JdbcProductRepository repo = new JdbcProductRepository();
        ProductRecord product = new ProductRecord("PTEST2", "Update Product", "shoes",
                50.0, ProductStatus.ACTIVE);
        
        repo.save(product);
        product.setPrice(75.0);
        repo.update(product);
        
        ProductRecord updated = repo.findById("PTEST2").orElseThrow();
        assertEquals(75.0, updated.getPrice(), 0.001);
    }

    @Test
    public void jdbcProductRepositoryDelete() {
        if (!dbAvailable) return;
        
        JdbcProductRepository repo = new JdbcProductRepository();
        JdbcStockRepository stockRepo = new JdbcStockRepository();
        
        ProductRecord product = new ProductRecord("PTEST3", "Delete Product", "clothes",
                30.0, ProductStatus.ACTIVE);
        
        repo.save(product);
        
        // Ensure no stock exists
        stockRepo.setQuantity("PTEST3", "M", 0);
        
        repo.delete("PTEST3");
        
        Optional<ProductRecord> found = repo.findById("PTEST3");
        assertFalse("Product should be deleted", found.isPresent());
    }

    // Stock Repository Tests
    @Test
    public void jdbcStockRepositorySetAndGetQuantity() {
        if (!dbAvailable) return;
        
        JdbcStockRepository repo = new JdbcStockRepository();
        
        repo.setQuantity("P001", "TESTSIZE", 25);
        int qty = repo.getQuantity("P001", "TESTSIZE");
        
        assertEquals("Should return set quantity", 25, qty);
        
        // Cleanup
        repo.setQuantity("P001", "TESTSIZE", 0);
    }

    @Test
    public void jdbcStockRepositoryIncreaseQuantity() {
        if (!dbAvailable) return;
        
        JdbcStockRepository repo = new JdbcStockRepository();
        
        repo.setQuantity("P001", "TESTINC", 10);
        repo.increaseQuantity("P001", "TESTINC", 5);
        
        int qty = repo.getQuantity("P001", "TESTINC");
        assertEquals("Should increase quantity", 15, qty);
        
        // Cleanup
        repo.setQuantity("P001", "TESTINC", 0);
    }

    @Test
    public void jdbcStockRepositoryDecreaseQuantity() {
        if (!dbAvailable) return;
        
        JdbcStockRepository repo = new JdbcStockRepository();
        
        repo.setQuantity("P001", "TESTDEC", 20);
        repo.decreaseQuantity("P001", "TESTDEC", 8);
        
        int qty = repo.getQuantity("P001", "TESTDEC");
        assertEquals("Should decrease quantity", 12, qty);
        
        // Cleanup
        repo.setQuantity("P001", "TESTDEC", 0);
    }

    @Test
    public void jdbcStockRepositoryGetTotalQuantity() {
        if (!dbAvailable) return;
        
        JdbcStockRepository repo = new JdbcStockRepository();
        
        // Use P001 which should have seeded stock
        int total = repo.getTotalQuantity("P001");
        
        assertTrue("Should have total stock", total >= 0);
    }

    // Transaction Repository Tests
    @Test
    public void jdbcTransactionRepositorySaveAndFind() {
        if (!dbAvailable) return;
        
        JdbcTransactionRepository repo = new JdbcTransactionRepository();
        
        TransactionHeader header = new TransactionHeader("TTEST1", LocalDateTime.now(),
                "MB001", "CU001", 100.0, "CASH");
        TransactionItem item = new TransactionItem("TTEST1", 1, "P001", "M", 2, 50.0);
        
        repo.saveTransaction(header, List.of(item));
        
        List<TransactionHeader> headers = repo.findAllHeaders();
        boolean found = headers.stream()
                .anyMatch(h -> "TTEST1".equals(h.getTransactionId()));
        assertTrue("Should find saved transaction", found);
        
        List<TransactionItem> items = repo.findItemsByTransaction("TTEST1");
        assertEquals("Should have 1 item", 1, items.size());
    }

    @Test
    public void jdbcTransactionRepositoryFindAll() {
        if (!dbAvailable) return;
        
        JdbcTransactionRepository repo = new JdbcTransactionRepository();
        List<TransactionHeader> headers = repo.findAllHeaders();
        
        assertNotNull("Should return list", headers);
    }

    @Test
    public void jdbcTransactionRepositoryWithMultipleItems() {
        if (!dbAvailable) return;
        
        JdbcTransactionRepository repo = new JdbcTransactionRepository();
        
        TransactionHeader header = new TransactionHeader("TTEST2", LocalDateTime.now(),
                "MB001", "CU001", 200.0, "WALLET");
        TransactionItem i1 = new TransactionItem("TTEST2", 1, "P001", "M", 1, 19.90);
        TransactionItem i2 = new TransactionItem("TTEST2", 2, "P002", "42", 1, 120.00);
        
        repo.saveTransaction(header, List.of(i1, i2));
        
        List<TransactionItem> items = repo.findItemsByTransaction("TTEST2");
        assertEquals("Should have 2 items", 2, items.size());
    }
}

