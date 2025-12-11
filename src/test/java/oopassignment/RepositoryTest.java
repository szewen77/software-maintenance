package oopassignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;
import oopassignment.domain.member.CustomerRecord;
import oopassignment.domain.member.MemberRecord;
import oopassignment.domain.member.MemberStatus;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.ProductStatus;
import oopassignment.domain.product.StockItem;
import oopassignment.repository.CustomerRepository;
import oopassignment.repository.EmployeeRepository;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.impl.InMemoryCustomerRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RepositoryTest {

    private PasswordHasher hasher;

    @Before
    public void setUp() {
        hasher = new PasswordHasher();
    }

    // Employee Repository Tests
    @Test
    public void employeeRepositorySaveAndFind() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        EmployeeRecord emp = new EmployeeRecord("E999", "testuser", "hash", 
                Role.STAFF, 3000.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        repo.save(emp);
        Optional<EmployeeRecord> found = repo.findById("E999");
        
        assertTrue("Should find saved employee", found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    public void employeeRepositoryFindByUsername() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        EmployeeRecord emp = new EmployeeRecord("E998", "findme", "hash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        repo.save(emp);
        Optional<EmployeeRecord> found = repo.findByUsername("findme");
        
        assertTrue("Should find by username", found.isPresent());
        assertEquals("E998", found.get().getId());
    }

    @Test
    public void employeeRepositoryUpdate() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        EmployeeRecord emp = new EmployeeRecord("E997", "updateme", "hash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        repo.save(emp);
        emp.setBaseSalary(3500.0);
        repo.update(emp);
        
        EmployeeRecord updated = repo.findById("E997").orElseThrow();
        assertEquals(3500.0, updated.getBaseSalary(), 0.001);
    }

    @Test
    public void employeeRepositoryFindAll() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        List<EmployeeRecord> all = repo.findAll();
        
        // Should have seeded employees
        assertFalse("Should have employees", all.isEmpty());
    }

    @Test
    public void employeeRepositoryCountByRoleAndStatus() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        long count = repo.countByRoleAndStatus(Role.MANAGER, EmploymentStatus.ACTIVE);
        
        // Should have at least 1 active manager from seed data
        assertTrue("Should have active managers", count >= 1);
    }

    @Test
    public void employeeRepositoryFindByRole() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        List<EmployeeRecord> managers = repo.findByRole(Role.MANAGER);
        
        assertFalse("Should find managers", managers.isEmpty());
    }

    // Member Repository Tests
    @Test
    public void memberRepositorySaveAndFind() {
        MemberRepository repo = new InMemoryMemberRepository();
        MemberRecord member = new MemberRecord("MB999", "Test Member", "999999999999",
                50.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        Optional<MemberRecord> found = repo.findById("MB999");
        
        assertTrue("Should find saved member", found.isPresent());
        assertEquals("Test Member", found.get().getName());
    }

    @Test
    public void memberRepositoryFindByIc() {
        MemberRepository repo = new InMemoryMemberRepository();
        MemberRecord member = new MemberRecord("MB998", "IC Test", "888888888888",
                100.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        Optional<MemberRecord> found = repo.findByIc("888888888888");
        
        assertTrue("Should find by IC", found.isPresent());
        assertEquals("MB998", found.get().getMemberId());
    }

    @Test
    public void memberRepositoryUpdate() {
        MemberRepository repo = new InMemoryMemberRepository();
        MemberRecord member = new MemberRecord("MB997", "Update Test", "777777777777",
                50.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(member);
        member.setCreditBalance(150.0);
        repo.update(member);
        
        MemberRecord updated = repo.findById("MB997").orElseThrow();
        assertEquals(150.0, updated.getCreditBalance(), 0.001);
    }

    @Test
    public void memberRepositorySearchByName() {
        MemberRepository repo = new InMemoryMemberRepository();
        MemberRecord m1 = new MemberRecord("MB996", "John Smith", "666666666666",
                0, LocalDate.now(), MemberStatus.ACTIVE);
        MemberRecord m2 = new MemberRecord("MB995", "John Doe", "555555555555",
                0, LocalDate.now(), MemberStatus.ACTIVE);
        
        repo.save(m1);
        repo.save(m2);
        
        List<MemberRecord> results = repo.searchByName("John");
        assertTrue("Should find multiple Johns", results.size() >= 2);
    }

    @Test
    public void memberRepositoryFindAll() {
        MemberRepository repo = new InMemoryMemberRepository();
        List<MemberRecord> all = repo.findAll();
        
        assertNotNull("Find all should not return null", all);
    }

    // Customer Repository Tests
    @Test
    public void customerRepositorySaveAndFind() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerRecord customer = new CustomerRecord("CU999", "Test Customer",
                LocalDate.now(), null);
        
        repo.save(customer);
        Optional<CustomerRecord> found = repo.findById("CU999");
        
        assertTrue("Should find saved customer", found.isPresent());
        assertEquals("Test Customer", found.get().getName());
    }

    @Test
    public void customerRepositoryUpdate() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerRecord customer = new CustomerRecord("CU998", "Update Customer",
                LocalDate.now(), null);
        
        repo.save(customer);
        customer.setLastPurchaseDate(LocalDate.now());
        repo.update(customer);
        
        CustomerRecord updated = repo.findById("CU998").orElseThrow();
        assertNotNull("Last purchase date should be set", updated.getLastPurchaseDate());
    }

    @Test
    public void customerRepositoryFindAll() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        List<CustomerRecord> all = repo.findAll();
        
        assertNotNull("Find all should not return null", all);
    }

    // Product Repository Tests
    @Test
    public void productRepositorySaveAndFind() {
        ProductRepository repo = new InMemoryProductRepository();
        ProductRecord product = new ProductRecord("P999", "Test Product", "clothes",
                99.90, ProductStatus.ACTIVE);
        
        repo.save(product);
        Optional<ProductRecord> found = repo.findById("P999");
        
        assertTrue("Should find saved product", found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

    @Test
    public void productRepositoryUpdate() {
        ProductRepository repo = new InMemoryProductRepository();
        ProductRecord product = new ProductRecord("P998", "Update Product", "shoes",
                50.0, ProductStatus.ACTIVE);
        
        repo.save(product);
        product.setPrice(75.0);
        repo.update(product);
        
        ProductRecord updated = repo.findById("P998").orElseThrow();
        assertEquals(75.0, updated.getPrice(), 0.001);
    }

    @Test
    public void productRepositoryDelete() {
        ProductRepository repo = new InMemoryProductRepository();
        ProductRecord product = new ProductRecord("P997", "Delete Product", "clothes",
                30.0, ProductStatus.ACTIVE);
        
        repo.save(product);
        repo.delete("P997");
        
        Optional<ProductRecord> found = repo.findById("P997");
        assertFalse("Product should be deleted", found.isPresent());
    }

    @Test
    public void productRepositoryFindAll() {
        ProductRepository repo = new InMemoryProductRepository();
        List<ProductRecord> all = repo.findAll();
        
        // Should have seeded products
        assertFalse("Should have products", all.isEmpty());
    }

    // Stock Repository Tests
    @Test
    public void stockRepositorySetAndGetQuantity() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P999", "XL", 20);
        int qty = repo.getQuantity("P999", "XL");
        
        assertEquals("Should return set quantity", 20, qty);
    }

    @Test
    public void stockRepositoryIncreaseQuantity() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P998", "M", 10);
        repo.increaseQuantity("P998", "M", 5);
        
        int qty = repo.getQuantity("P998", "M");
        assertEquals("Should increase quantity", 15, qty);
    }

    @Test
    public void stockRepositoryDecreaseQuantity() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P997", "L", 20);
        repo.decreaseQuantity("P997", "L", 8);
        
        int qty = repo.getQuantity("P997", "L");
        assertEquals("Should decrease quantity", 12, qty);
    }

    @Test
    public void stockRepositoryGetTotalQuantity() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P996", "S", 5);
        repo.setQuantity("P996", "M", 10);
        repo.setQuantity("P996", "L", 8);
        
        int total = repo.getTotalQuantity("P996");
        assertEquals("Should sum all sizes", 23, total);
    }

    @Test
    public void stockRepositoryFindByProductId() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P995", "S", 5);
        repo.setQuantity("P995", "M", 10);
        
        List<StockItem> items = repo.findByProductId("P995");
        assertTrue("Should find stock items", items.size() >= 2);
    }

    @Test
    public void stockRepositoryMultipleSizes() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.setQuantity("P994", "S", 5);
        repo.setQuantity("P994", "M", 10);
        repo.setQuantity("P994", "L", 8);
        
        List<StockItem> items = repo.findByProductId("P994");
        assertEquals("Should have 3 sizes", 3, items.size());
    }

    @Test
    public void stockRepositoryGetQuantityForNonExistent() {
        StockRepository repo = new InMemoryStockRepository();
        
        int qty = repo.getQuantity("PXXX", "XL");
        assertEquals("Should return 0 for non-existent", 0, qty);
    }

    @Test
    public void stockRepositoryIncreaseFromZero() {
        StockRepository repo = new InMemoryStockRepository();
        
        repo.increaseQuantity("P993", "M", 10);
        int qty = repo.getQuantity("P993", "M");
        
        assertEquals("Should increase from 0", 10, qty);
    }

    @Test
    public void employeeRepositoryFindByIdReturnsEmpty() {
        EmployeeRepository repo = new InMemoryEmployeeRepository(hasher);
        Optional<EmployeeRecord> found = repo.findById("NONEXISTENT");
        assertFalse("Should return empty for non-existent ID", found.isPresent());
    }

    @Test
    public void memberRepositoryFindByIdReturnsEmpty() {
        MemberRepository repo = new InMemoryMemberRepository();
        Optional<MemberRecord> found = repo.findById("NONEXISTENT");
        assertFalse("Should return empty for non-existent ID", found.isPresent());
    }

    @Test
    public void customerRepositoryFindByIdReturnsEmpty() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        Optional<CustomerRecord> found = repo.findById("NONEXISTENT");
        assertFalse("Should return empty for non-existent ID", found.isPresent());
    }

    @Test
    public void productRepositoryFindByIdReturnsEmpty() {
        ProductRepository repo = new InMemoryProductRepository();
        Optional<ProductRecord> found = repo.findById("NONEXISTENT");
        assertFalse("Should return empty for non-existent ID", found.isPresent());
    }

    @Test
    public void stockRepositoryFindByProductIdReturnsEmpty() {
        StockRepository repo = new InMemoryStockRepository();
        List<StockItem> items = repo.findByProductId("NONEXISTENT");
        assertTrue("Should return empty list", items.isEmpty());
    }
}

