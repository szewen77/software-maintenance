package oopassignment;

import java.util.ArrayList;
import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for Order domain classes to boost branch coverage
 */
public class OrderDomainTest {

    @Test
    public void orderItemRequestCreation() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 5);
        
        assertEquals("P001", item.getProductId());
        assertEquals("M", item.getSize());
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void orderItemRequestWithDifferentSizes() {
        OrderItemRequest small = new OrderItemRequest("P002", "S", 1);
        OrderItemRequest large = new OrderItemRequest("P002", "L", 3);
        OrderItemRequest custom = new OrderItemRequest("P003", "42", 2);
        
        assertEquals("S", small.getSize());
        assertEquals("L", large.getSize());
        assertEquals("42", custom.getSize());
    }

    @Test
    public void orderRequestWithMember() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 1));
        
        OrderRequest request = new OrderRequest("MB001", items, "CASH");
        
        assertEquals("MB001", request.getMemberId());
        assertEquals("MEMBER", request.getCustomerType());
        assertEquals(1, request.getItems().size());
        assertEquals("CASH", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithoutMember() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 1));
        
        OrderRequest request = new OrderRequest(null, items, "CARD");
        
        assertNull("Member ID should be null", request.getMemberId());
        assertEquals("WALK-IN", request.getCustomerType());
        assertEquals("CARD", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithMultipleItems() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 2));
        items.add(new OrderItemRequest("P002", "42", 1));
        items.add(new OrderItemRequest("P001", "L", 3));
        
        OrderRequest request = new OrderRequest("MB002", items, "WALLET");
        
        assertEquals(3, request.getItems().size());
        assertEquals("WALLET", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithEmptyMemberId() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 1));
        
        OrderRequest request = new OrderRequest("", items, "CASH");
        
        assertEquals("", request.getMemberId());
        assertEquals("WALK-IN", request.getCustomerType());
    }

    @Test
    public void orderResultWithDiscount() {
        OrderResult result = new OrderResult("T001", 100.0, 5.0, 95.0, true);
        
        assertEquals("T001", result.getTransactionId());
        assertEquals(100.0, result.getSubtotal(), 0.001);
        assertEquals(5.0, result.getDiscount(), 0.001);
        assertEquals(95.0, result.getTotal(), 0.001);
        assertTrue("Member discount should be applied", result.isMemberDiscountApplied());
    }

    @Test
    public void orderResultWithoutDiscount() {
        OrderResult result = new OrderResult("T002", 50.0, 0.0, 50.0, false);
        
        assertEquals("T002", result.getTransactionId());
        assertEquals(50.0, result.getSubtotal(), 0.001);
        assertEquals(0.0, result.getDiscount(), 0.001);
        assertEquals(50.0, result.getTotal(), 0.001);
        assertFalse("Member discount should not be applied", result.isMemberDiscountApplied());
    }

    @Test
    public void orderResultWithLargeDiscount() {
        OrderResult result = new OrderResult("T003", 1000.0, 50.0, 950.0, true);
        
        assertEquals(50.0, result.getDiscount(), 0.001);
        assertEquals(950.0, result.getTotal(), 0.001);
    }

    @Test
    public void orderResultWithSmallAmount() {
        OrderResult result = new OrderResult("T004", 10.0, 0.5, 9.5, true);
        
        assertEquals(10.0, result.getSubtotal(), 0.001);
        assertEquals(0.5, result.getDiscount(), 0.001);
        assertEquals(9.5, result.getTotal(), 0.001);
    }

    @Test
    public void orderRequestWithCashPayment() {
        List<OrderItemRequest> items = List.of(new OrderItemRequest("P001", "M", 1));
        OrderRequest request = new OrderRequest("MB003", items, "CASH");
        
        assertEquals("CASH", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithCardPayment() {
        List<OrderItemRequest> items = List.of(new OrderItemRequest("P001", "M", 1));
        OrderRequest request = new OrderRequest("MB004", items, "CARD");
        
        assertEquals("CARD", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithWalletPayment() {
        List<OrderItemRequest> items = List.of(new OrderItemRequest("P001", "M", 1));
        OrderRequest request = new OrderRequest("MB005", items, "WALLET");
        
        assertEquals("WALLET", request.getPaymentMethod());
    }

    @Test
    public void orderItemRequestWithVariousQuantities() {
        OrderItemRequest one = new OrderItemRequest("P001", "M", 1);
        OrderItemRequest many = new OrderItemRequest("P002", "L", 100);
        
        assertEquals(1, one.getQuantity());
        assertEquals(100, many.getQuantity());
    }

    @Test
    public void orderResultMemberDiscountFlagTrue() {
        OrderResult result = new OrderResult("T005", 100.0, 5.0, 95.0, true);
        assertTrue(result.isMemberDiscountApplied());
    }

    @Test
    public void orderResultMemberDiscountFlagFalse() {
        OrderResult result = new OrderResult("T006", 100.0, 0.0, 100.0, false);
        assertFalse(result.isMemberDiscountApplied());
    }

    @Test
    public void orderRequestGettersReturnCorrectValues() {
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 2));
        
        OrderRequest request = new OrderRequest("MB006", items, "CASH");
        
        assertNotNull(request.getMemberId());
        assertNotNull(request.getCustomerType());
        assertNotNull(request.getItems());
        assertNotNull(request.getPaymentMethod());
        assertFalse(request.getItems().isEmpty());
    }

    @Test
    public void orderResultGettersReturnCorrectValues() {
        OrderResult result = new OrderResult("T007", 200.0, 10.0, 190.0, true);
        
        assertNotNull(result.getTransactionId());
        assertTrue(result.getSubtotal() > 0);
        assertTrue(result.getDiscount() >= 0);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    public void orderRequestWithNullItems() {
        // Test line 15: items == null ? new ArrayList<>() : new ArrayList<>(items)
        OrderRequest request = new OrderRequest("MB007", null, "CASH");
        
        assertNotNull("Items should not be null", request.getItems());
        assertTrue("Items should be empty list", request.getItems().isEmpty());
        assertEquals(0, request.getItems().size());
    }

    @Test
    public void orderRequestWithNullPaymentMethod() {
        // Test line 16: paymentMethod == null ? "CASH" : paymentMethod
        List<OrderItemRequest> items = List.of(new OrderItemRequest("P001", "M", 1));
        OrderRequest request = new OrderRequest("MB008", items, null);
        
        assertEquals("CASH", request.getPaymentMethod());
    }

    @Test
    public void orderRequestItemsAreImmutable() {
        // Test line 28: return new ArrayList<>(items) - ensures defensive copy
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(new OrderItemRequest("P001", "M", 1));
        
        OrderRequest request = new OrderRequest("MB009", items, "CASH");
        List<OrderItemRequest> returnedItems = request.getItems();
        
        // Modify the returned list
        returnedItems.add(new OrderItemRequest("P002", "L", 5));
        
        // Original should not be affected
        assertEquals(1, request.getItems().size());
    }

    @Test
    public void orderRequestWithNullItemsAndPaymentMethod() {
        // Test both null branches together
        OrderRequest request = new OrderRequest("MB010", null, null);
        
        assertNotNull(request.getItems());
        assertTrue(request.getItems().isEmpty());
        assertEquals("CASH", request.getPaymentMethod());
    }

    @Test
    public void orderRequestWithEmptyItemsList() {
        List<OrderItemRequest> emptyItems = new ArrayList<>();
        OrderRequest request = new OrderRequest("MB011", emptyItems, "CARD");
        
        assertEquals(0, request.getItems().size());
        assertTrue(request.getItems().isEmpty());
    }

    @Test
    public void orderItemRequestAllGetters() {
        OrderItemRequest item = new OrderItemRequest("PROD123", "XL", 99);
        
        assertEquals("PROD123", item.getProductId());
        assertEquals("XL", item.getSize());
        assertEquals(99, item.getQuantity());
    }

    @Test
    public void orderResultWithNoDiscount() {
        OrderResult result = new OrderResult("T008", 100.0, 0.0, 100.0, false);
        
        assertEquals(100.0, result.getSubtotal(), 0.001);
        assertEquals(0.0, result.getDiscount(), 0.001);
        assertEquals(100.0, result.getTotal(), 0.001);
        assertFalse(result.isMemberDiscountApplied());
        assertNotNull("Transaction ID should be set", result.getTransactionId());
    }

    @Test
    public void orderResultWithDiscountApplied() {
        OrderResult result = new OrderResult("T009", 50.0, 2.5, 47.5, true);
        
        assertEquals(50.0, result.getSubtotal(), 0.001);
        assertEquals(2.5, result.getDiscount(), 0.001);
        assertEquals(47.5, result.getTotal(), 0.001);
        assertTrue(result.isMemberDiscountApplied());
    }
}

