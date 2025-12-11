package oopassignment;

import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import oopassignment.service.InventoryService;
import oopassignment.service.OrderService;
import oopassignment.service.PricingService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class OrderServiceTest {

    private OrderService orderService;
    private PricingService pricingService;

    @Before
    public void setUp() {
        ProductRepository productRepository = new InMemoryProductRepository();
        StockRepository stockRepository = new InMemoryStockRepository();
        InventoryService inventoryService = new InventoryService(stockRepository);
        pricingService = new PricingService();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        orderService = new OrderService(productRepository, inventoryService, pricingService, transactionRepository);
    }

    @Test
    public void memberDiscountApplied() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 2);
        OrderRequest request = new OrderRequest("MB001", null, List.of(item), "CASH");
        OrderResult result = orderService.placeOrder(request);

        double expectedSubtotal = 19.90 * 2;
        double expectedTotal = expectedSubtotal * (1 - pricingService.getMemberDiscountRate());

        assertTrue("Member discount should apply", result.isMemberDiscountApplied());
        assertEquals("Subtotal mismatch", expectedSubtotal, result.getSubtotal(), 0.0001);
        assertEquals("Total mismatch with discount", expectedTotal, result.getTotal(), 0.0001);
    }

    @Test
    public void nonMemberPurchaseHasNoDiscount() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1);
        OrderRequest request = new OrderRequest(null, "CU002", List.of(item), "CARD");
        OrderResult result = orderService.placeOrder(request);

        assertFalse("No discount for non-member", result.isMemberDiscountApplied());
        assertEquals("Total should equal unit price", 19.90, result.getTotal(), 0.0001);
    }

    @Test
    public void insufficientStockThrows() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1000);
        OrderRequest request = new OrderRequest(null, "CU003", List.of(item), "CASH");
        assertThrows("Should fail when stock is insufficient",
                InsufficientStockException.class,
                () -> orderService.placeOrder(request));
    }

    @Test
    public void mixedItemsTotalMatches() {
        OrderItemRequest item1 = new OrderItemRequest("P001", "M", 1);
        OrderItemRequest item2 = new OrderItemRequest("P002", "42", 1);
        OrderRequest request = new OrderRequest(null, "CU004", List.of(item1, item2), "CASH");
        OrderResult result = orderService.placeOrder(request);

        double expectedTotal = 19.90 + 120.00;
        assertEquals("Total should sum mixed items", expectedTotal, result.getTotal(), 0.0001);
    }

    @Test
    public void emptyOrderRejected() {
        OrderRequest empty = new OrderRequest(null, null, List.of(), "CASH");
        assertThrows("Empty orders should be rejected",
                InvalidInputException.class,
                () -> orderService.placeOrder(empty));
    }
}
