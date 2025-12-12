package oopassignment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;
import oopassignment.domain.member.MemberRecord;
import oopassignment.domain.member.MemberStatus;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.ProductStatus;
import oopassignment.domain.product.StockItem;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DomainRecordsTest {

    @Test
    public void employeeRecordCreation() {
        EmployeeRecord employee = new EmployeeRecord("E001", "manager", "hash123", 
                Role.MANAGER, 5000.0, 0.20, null, EmploymentStatus.ACTIVE);
        
        assertEquals("E001", employee.getId());
        assertEquals("manager", employee.getUsername());
        assertEquals(Role.MANAGER, employee.getRole());
        assertEquals(5000.0, employee.getBaseSalary(), 0.001);
        assertEquals(0.20, employee.getBonusRate(), 0.001);
        assertEquals(EmploymentStatus.ACTIVE, employee.getStatus());
    }

    @Test
    public void employeeRecordSetters() {
        EmployeeRecord employee = new EmployeeRecord("E002", "staff", "hash", 
                Role.STAFF, 3000.0, 0.10, "M001", EmploymentStatus.ACTIVE);
        
        employee.setRole(Role.MANAGER);
        employee.setBaseSalary(4000.0);
        employee.setBonusRate(0.20);
        employee.setStatus(EmploymentStatus.INACTIVE);
        
        assertEquals(Role.MANAGER, employee.getRole());
        assertEquals(4000.0, employee.getBaseSalary(), 0.001);
        assertEquals(0.20, employee.getBonusRate(), 0.001);
        assertEquals(EmploymentStatus.INACTIVE, employee.getStatus());
    }

    @Test
    public void employeeIsActiveWhenStatusActive() {
        EmployeeRecord employee = new EmployeeRecord("E003", "active", "hash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        assertTrue("Employee should be active", employee.isActive());
    }

    @Test
    public void employeeIsNotActiveWhenStatusInactive() {
        EmployeeRecord employee = new EmployeeRecord("E004", "inactive", "hash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.INACTIVE);
        
        assertFalse("Employee should not be active", employee.isActive());
    }

    @Test
    public void employeePasswordHashSetter() {
        EmployeeRecord employee = new EmployeeRecord("E005", "test", "oldhash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        employee.setPasswordHash("newhash");
        assertEquals("newhash", employee.getPasswordHash());
    }

    @Test
    public void employeeUplineIdSetter() {
        EmployeeRecord employee = new EmployeeRecord("E006", "test", "hash", 
                Role.STAFF, 2500.0, 0.10, "M001", EmploymentStatus.ACTIVE);
        
        employee.setUplineId("M002");
        assertEquals("M002", employee.getUplineId());
    }

    @Test
    public void memberRecordCreation() {
        MemberRecord member = new MemberRecord("MB001", "John Doe", "900101011234", 
                100.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        assertEquals("MB001", member.getMemberId());
        assertEquals("John Doe", member.getName());
        assertEquals("900101011234", member.getIcNumber());
        assertEquals(100.0, member.getCreditBalance(), 0.001);
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
    }

    @Test
    public void memberRecordSetters() {
        MemberRecord member = new MemberRecord("MB002", "Jane", "950101011234", 
                50.0, LocalDate.now(), MemberStatus.ACTIVE);
        
        member.setName("Jane Smith");
        member.setCreditBalance(75.0);
        member.setStatus(MemberStatus.INACTIVE);
        
        assertEquals("Jane Smith", member.getName());
        assertEquals(75.0, member.getCreditBalance(), 0.001);
        assertEquals(MemberStatus.INACTIVE, member.getStatus());
    }


    @Test
    public void productRecordCreation() {
        ProductRecord product = new ProductRecord("P001", "T-Shirt", "clothes", 
                29.90, ProductStatus.ACTIVE);
        
        assertEquals("P001", product.getProductId());
        assertEquals("T-Shirt", product.getName());
        assertEquals("clothes", product.getCategory());
        assertEquals(29.90, product.getPrice(), 0.001);
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    public void productRecordSetters() {
        ProductRecord product = new ProductRecord("P002", "Shoes", "shoes", 
                120.0, ProductStatus.ACTIVE);
        
        product.setName("Running Shoes");
        product.setPrice(150.0);
        product.setStatus(ProductStatus.INACTIVE);
        
        assertEquals("Running Shoes", product.getName());
        assertEquals(150.0, product.getPrice(), 0.001);
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    public void productRecordSetName() {
        ProductRecord product = new ProductRecord("P003", "Old Name", "clothes", 
                20.0, ProductStatus.ACTIVE);
        
        product.setName("New Name");
        assertEquals("New Name", product.getName());
    }

    @Test
    public void productRecordSetCategory() {
        ProductRecord product = new ProductRecord("P004", "Sneakers", "shoes", 
                100.0, ProductStatus.ACTIVE);
        
        product.setCategory("sports");
        assertEquals("sports", product.getCategory());
    }

    @Test
    public void productRecordSetPrice() {
        ProductRecord product = new ProductRecord("P005", "Jacket", "clothes", 
                50.0, ProductStatus.ACTIVE);
        
        product.setPrice(75.50);
        assertEquals(75.50, product.getPrice(), 0.001);
    }

    @Test
    public void productRecordSetStatus() {
        ProductRecord product = new ProductRecord("P006", "Boots", "shoes", 
                150.0, ProductStatus.ACTIVE);
        
        product.setStatus(ProductStatus.INACTIVE);
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    public void productRecordAllSettersIndividually() {
        ProductRecord product = new ProductRecord("P007", "Original", "original", 
                100.0, ProductStatus.ACTIVE);
        
        product.setName("Updated Name");
        assertEquals("Updated Name", product.getName());
        
        product.setCategory("Updated Category");
        assertEquals("Updated Category", product.getCategory());
        
        product.setPrice(200.0);
        assertEquals(200.0, product.getPrice(), 0.001);
        
        product.setStatus(ProductStatus.INACTIVE);
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    public void stockItemCreation() {
        StockItem stock = new StockItem("P001", "M", 10);
        
        assertEquals("P001", stock.getProductId());
        assertEquals("M", stock.getSize());
        assertEquals(10, stock.getQuantity());
    }

    @Test
    public void stockItemSetters() {
        StockItem stock = new StockItem("P002", "L", 5);
        stock.setQuantity(8);
        
        assertEquals(8, stock.getQuantity());
    }

    @Test
    public void stockItemSetQuantityMultipleTimes() {
        StockItem stock = new StockItem("P003", "XL", 10);
        
        stock.setQuantity(20);
        assertEquals(20, stock.getQuantity());
        
        stock.setQuantity(5);
        assertEquals(5, stock.getQuantity());
        
        stock.setQuantity(100);
        assertEquals(100, stock.getQuantity());
    }

    @Test
    public void stockItemAllGetters() {
        StockItem stock = new StockItem("PROD999", "42", 77);
        
        assertEquals("PROD999", stock.getProductId());
        assertEquals("42", stock.getSize());
        assertEquals(77, stock.getQuantity());
    }

    @Test
    public void stockItemWithZeroQuantity() {
        StockItem stock = new StockItem("P004", "S", 0);
        assertEquals(0, stock.getQuantity());
        
        stock.setQuantity(10);
        assertEquals(10, stock.getQuantity());
    }

    @Test
    public void transactionHeaderCreation() {
        TransactionHeader header = new TransactionHeader("T001", LocalDateTime.now(), 
                "MB001", "MEMBER", 95.0, "CASH");
        
        assertEquals("T001", header.getTransactionId());
        assertEquals("MB001", header.getMemberId());
        assertEquals("MEMBER", header.getCustomerType());
        assertEquals(95.0, header.getTotalAmount(), 0.001);
        assertEquals("CASH", header.getPaymentMethod());
    }

    @Test
    public void transactionItemCreation() {
        TransactionItem item = new TransactionItem("T001", 1, "P001", "M", 2, 29.90);
        
        assertEquals("T001", item.getTransactionId());
        assertEquals(1, item.getLineNo());
        assertEquals("P001", item.getProductId());
        assertEquals("M", item.getSize());
        assertEquals(2, item.getQuantity());
        assertEquals(29.90, item.getUnitPrice(), 0.001);
    }

    @Test
    public void orderItemRequestCreation() {
        OrderItemRequest request = new OrderItemRequest("P001", "L", 3);
        
        assertEquals("P001", request.getProductId());
        assertEquals("L", request.getSize());
        assertEquals(3, request.getQuantity());
    }

    @Test
    public void orderRequestCreation() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 1));
        
        OrderRequest request = new OrderRequest("MB001", items, "CARD");
        
        assertEquals("MB001", request.getMemberId());
        assertEquals("MEMBER", request.getCustomerType());
        assertEquals(1, request.getItems().size());
        assertEquals("CARD", request.getPaymentMethod());
    }

    @Test
    public void orderResultCreation() {
        OrderResult result = new OrderResult("T001", 100.0, 5.0, 95.0, true);
        
        assertEquals("T001", result.getTransactionId());
        assertEquals(100.0, result.getSubtotal(), 0.001);
        assertEquals(5.0, result.getDiscount(), 0.001);
        assertEquals(95.0, result.getTotal(), 0.001);
        assertTrue(result.isMemberDiscountApplied());
    }

    @Test
    public void orderResultWithoutDiscount() {
        OrderResult result = new OrderResult("T002", 50.0, 0.0, 50.0, false);
        
        assertFalse(result.isMemberDiscountApplied());
        assertEquals(0.0, result.getDiscount(), 0.001);
    }

    @Test
    public void memberPurchaseCreation() {
        MemberPurchase purchase = new MemberPurchase("T001", LocalDateTime.now(), 100.0, "CASH");
        
        assertEquals("T001", purchase.getTransactionId());
        assertEquals(100.0, purchase.getAmount(), 0.001);
        assertEquals("CASH", purchase.getPaymentMethod());
        assertNotNull(purchase.getDateTime());
    }

    @Test
    public void salesSummaryCreation() {
        SalesSummary summary = new SalesSummary(1000.0, 10);
        
        assertEquals(10, summary.getTransactionCount());
        assertEquals(1000.0, summary.getTotalAmount(), 0.001);
    }

    @Test
    public void salesSummaryGetAveragePerTransaction() {
        SalesSummary summary = new SalesSummary(1000.0, 10);
        
        double average = summary.getAveragePerTransaction();
        assertEquals(100.0, average, 0.001);
    }

    @Test
    public void salesSummaryGetAverageWhenZeroTransactions() {
        SalesSummary summary = new SalesSummary(1000.0, 0);
        
        double average = summary.getAveragePerTransaction();
        assertEquals(0.0, average, 0.001);
    }

    @Test
    public void salesSummaryGetAverageWithSmallAmount() {
        SalesSummary summary = new SalesSummary(45.50, 5);
        
        double average = summary.getAveragePerTransaction();
        assertEquals(9.1, average, 0.01);
    }

    @Test
    public void roleEnumValues() {
        assertEquals(Role.MANAGER, Role.valueOf("MANAGER"));
        assertEquals(Role.STAFF, Role.valueOf("STAFF"));
    }

    @Test
    public void employmentStatusEnumValues() {
        assertEquals(EmploymentStatus.ACTIVE, EmploymentStatus.valueOf("ACTIVE"));
        assertEquals(EmploymentStatus.INACTIVE, EmploymentStatus.valueOf("INACTIVE"));
    }

    @Test
    public void memberStatusEnumValues() {
        assertEquals(MemberStatus.ACTIVE, MemberStatus.valueOf("ACTIVE"));
        assertEquals(MemberStatus.INACTIVE, MemberStatus.valueOf("INACTIVE"));
    }

    @Test
    public void productStatusEnumValues() {
        assertEquals(ProductStatus.ACTIVE, ProductStatus.valueOf("ACTIVE"));
        assertEquals(ProductStatus.INACTIVE, ProductStatus.valueOf("INACTIVE"));
    }
}

