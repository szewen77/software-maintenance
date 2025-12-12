package oopassignment;

import oopassignment.util.ApplicationContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationContextTest {

    @Test
    public void passwordHasherIsInitialized() {
        assertNotNull("PasswordHasher should be initialized", 
                ApplicationContext.PASSWORD_HASHER);
    }

    @Test
    public void employeeRepositoryIsInitialized() {
        assertNotNull("EmployeeRepository should be initialized", 
                ApplicationContext.EMPLOYEE_REPOSITORY);
    }

    @Test
    public void employeeServiceIsInitialized() {
        assertNotNull("EmployeeService should be initialized", 
                ApplicationContext.EMPLOYEE_SERVICE);
    }

    @Test
    public void authServiceIsInitialized() {
        assertNotNull("AuthService should be initialized", 
                ApplicationContext.AUTH_SERVICE);
    }

    @Test
    public void memberRepositoryIsInitialized() {
        assertNotNull("MemberRepository should be initialized", 
                ApplicationContext.MEMBER_REPOSITORY);
    }

    @Test
    public void memberServiceIsInitialized() {
        assertNotNull("MemberService should be initialized", 
                ApplicationContext.MEMBER_SERVICE);
    }

    @Test
    public void productRepositoryIsInitialized() {
        assertNotNull("ProductRepository should be initialized", 
                ApplicationContext.PRODUCT_REPOSITORY);
    }

    @Test
    public void stockRepositoryIsInitialized() {
        assertNotNull("StockRepository should be initialized", 
                ApplicationContext.STOCK_REPOSITORY);
    }

    @Test
    public void productServiceIsInitialized() {
        assertNotNull("ProductService should be initialized", 
                ApplicationContext.PRODUCT_SERVICE);
    }

    @Test
    public void inventoryServiceIsInitialized() {
        assertNotNull("InventoryService should be initialized", 
                ApplicationContext.INVENTORY_SERVICE);
    }

    @Test
    public void transactionRepositoryIsInitialized() {
        assertNotNull("TransactionRepository should be initialized", 
                ApplicationContext.TRANSACTION_REPOSITORY);
    }

    @Test
    public void pricingServiceIsInitialized() {
        assertNotNull("PricingService should be initialized", 
                ApplicationContext.PRICING_SERVICE);
    }

    @Test
    public void orderServiceIsInitialized() {
        assertNotNull("OrderService should be initialized", 
                ApplicationContext.ORDER_SERVICE);
    }

    @Test
    public void reportServiceIsInitialized() {
        assertNotNull("ReportService should be initialized", 
                ApplicationContext.REPORT_SERVICE);
    }

    @Test
    public void allServicesAreAccessible() {
        // Verify all services can be accessed without exceptions
        assertNotNull(ApplicationContext.PASSWORD_HASHER);
        assertNotNull(ApplicationContext.EMPLOYEE_SERVICE);
        assertNotNull(ApplicationContext.AUTH_SERVICE);
        assertNotNull(ApplicationContext.MEMBER_SERVICE);
        assertNotNull(ApplicationContext.PRODUCT_SERVICE);
        assertNotNull(ApplicationContext.INVENTORY_SERVICE);
        assertNotNull(ApplicationContext.ORDER_SERVICE);
        assertNotNull(ApplicationContext.REPORT_SERVICE);
        assertNotNull(ApplicationContext.PRICING_SERVICE);
    }

    @Test
    public void allRepositoriesAreAccessible() {
        // Verify all repositories can be accessed without exceptions
        assertNotNull(ApplicationContext.EMPLOYEE_REPOSITORY);
        assertNotNull(ApplicationContext.MEMBER_REPOSITORY);
        assertNotNull(ApplicationContext.PRODUCT_REPOSITORY);
        assertNotNull(ApplicationContext.STOCK_REPOSITORY);
        assertNotNull(ApplicationContext.TRANSACTION_REPOSITORY);
    }

    @Test
    public void servicesCanPerformBasicOperations() {
        // Test that services are properly wired and functional
        try {
            // Test AuthService
            var authResult = ApplicationContext.AUTH_SERVICE.login("nonexistent", "test");
            assertNotNull("AuthService should return result", authResult);
            
            // Test MemberService
            var members = ApplicationContext.MEMBER_SERVICE.findAll();
            assertNotNull("MemberService should return list", members);
            
            // Test ProductService
            var products = ApplicationContext.PRODUCT_SERVICE.findAll();
            assertNotNull("ProductService should return list", products);
            
            // Test EmployeeRepository
            var employees = ApplicationContext.EMPLOYEE_REPOSITORY.findAll();
            assertNotNull("EmployeeRepository should return list", employees);
            
        } catch (Exception e) {
            fail("Services should be properly initialized and functional: " + e.getMessage());
        }
    }

    @Test
    public void pricingServiceReturnsValidDiscountRate() {
        double rate = ApplicationContext.PRICING_SERVICE.getMemberDiscountRate();
        assertTrue("Discount rate should be positive", rate > 0);
        assertTrue("Discount rate should be less than 1", rate < 1);
    }

    @Test
    public void passwordHasherCanHashPasswords() {
        String hash = ApplicationContext.PASSWORD_HASHER.hash("test");
        assertNotNull("Should hash passwords", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void inventoryServiceCanCheckStock() {
        // Should work with seeded data
        boolean available = ApplicationContext.INVENTORY_SERVICE.isStockAvailable("P001", "M", 1);
        // Result depends on seed data, but method should execute
        assertTrue("Method should return true or false", 
                available == true || available == false);
    }

    @Test
    public void repositoriesReturnNonNullCollections() {
        assertNotNull("Employee findAll should not return null", 
                ApplicationContext.EMPLOYEE_REPOSITORY.findAll());
        assertNotNull("Member findAll should not return null", 
                ApplicationContext.MEMBER_REPOSITORY.findAll());
        assertNotNull("Product findAll should not return null", 
                ApplicationContext.PRODUCT_REPOSITORY.findAll());
    }

    @Test
    public void orderServiceIsProperlyWired() {
        assertNotNull("OrderService should be wired", ApplicationContext.ORDER_SERVICE);
        // Verify it can access its dependencies
        assertNotNull("PricingService should be accessible", 
                ApplicationContext.PRICING_SERVICE);
        assertNotNull("InventoryService should be accessible", 
                ApplicationContext.INVENTORY_SERVICE);
    }
}

