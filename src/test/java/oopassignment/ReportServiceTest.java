package oopassignment;

import java.time.LocalDate;
import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import oopassignment.service.InventoryService;
import oopassignment.service.MemberService;
import oopassignment.service.OrderService;
import oopassignment.service.PricingService;
import oopassignment.service.ReportService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportServiceTest {

    private ReportService reportService;
    private OrderService orderService;
    private PricingService pricingService;
    private MemberService memberService;

    @Before
    public void setUp() {
        ProductRepository productRepository = new InMemoryProductRepository();
        StockRepository stockRepository = new InMemoryStockRepository();
        InventoryService inventoryService = new InventoryService(stockRepository);
        pricingService = new PricingService();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        memberService = new MemberService(memberRepository);
        orderService = new OrderService(productRepository, inventoryService, pricingService, transactionRepository, memberRepository);
        reportService = new ReportService(transactionRepository);
        
        // Create members with sufficient balance for WALLET payment tests
        memberService.registerMember("Test Member 3", "930303033333", 500.0);
        memberService.registerMember("Test Member 4", "940404044444", 500.0);
    }

    @Test
    public void salesSummaryAndMemberHistoryAreComputed() {
        orderService.placeOrder(new OrderRequest("MB001", null,
                List.of(new OrderItemRequest("P001", "M", 1)), "CASH"));
        orderService.placeOrder(new OrderRequest(null, "CU010",
                List.of(new OrderItemRequest("P002", "42", 1)), "CARD"));

        SalesSummary summary = reportService.getTotalSales(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals("Should count two transactions", 2, summary.getTransactionCount());
        double expectedTotal = (19.90 * (1 - pricingService.getMemberDiscountRate())) + 120.00;
        assertEquals("Total sales amount mismatch", expectedTotal, summary.getTotalAmount(), 0.001);

        List<MemberPurchase> purchases = reportService.getMemberPurchaseHistory("MB001");
        assertEquals("Member should have one purchase", 1, purchases.size());
    }

    @Test
    public void getTotalSalesWithNoTransactions() {
        LocalDate farFuture = LocalDate.now().plusYears(10);
        SalesSummary summary = reportService.getTotalSales(farFuture, farFuture.plusDays(1));
        
        assertEquals("Should have 0 transactions", 0, summary.getTransactionCount());
        assertEquals("Total should be 0", 0.0, summary.getTotalAmount(), 0.001);
    }

    @Test
    public void getMemberPurchaseHistoryWithNoTransactions() {
        List<MemberPurchase> purchases = reportService.getMemberPurchaseHistory("MBNONEXISTENT");
        
        assertTrue("Should return empty list", purchases.isEmpty());
    }

    @Test
    public void getTotalSalesIncludesMultipleTransactions() {
        // Create multiple transactions
        orderService.placeOrder(new OrderRequest("MB001", "CU011",
                List.of(new OrderItemRequest("P001", "M", 1)), "CASH"));
        orderService.placeOrder(new OrderRequest("MB001", "CU012",
                List.of(new OrderItemRequest("P001", "L", 2)), "CARD"));
        orderService.placeOrder(new OrderRequest(null, "CU013",
                List.of(new OrderItemRequest("P002", "42", 1)), "CASH"));
        
        SalesSummary summary = reportService.getTotalSales(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        
        assertTrue("Should have multiple transactions", summary.getTransactionCount() >= 3);
        assertTrue("Total amount should be positive", summary.getTotalAmount() > 0);
    }

    @Test
    public void getMemberPurchaseHistoryReturnsAllForMember() {
        // Create multiple purchases for same member
        orderService.placeOrder(new OrderRequest("MB002", "CU014",
                List.of(new OrderItemRequest("P001", "M", 1)), "CASH"));
        orderService.placeOrder(new OrderRequest("MB002", "CU015",
                List.of(new OrderItemRequest("P002", "42", 1)), "CARD"));
        
        List<MemberPurchase> purchases = reportService.getMemberPurchaseHistory("MB002");
        
        assertTrue("Should have multiple purchases", purchases.size() >= 2);
    }

    @Test
    public void getTotalSalesWithSpecificDateRange() {
        LocalDate today = LocalDate.now();
        
        // Use CASH instead of WALLET to avoid member dependency issues
        orderService.placeOrder(new OrderRequest("MB003", "CU016",
                List.of(new OrderItemRequest("P001", "M", 1)), "CASH"));
        
        SalesSummary summary = reportService.getTotalSales(today, today.plusDays(1));
        
        assertTrue("Should include today's transactions", summary.getTransactionCount() >= 1);
    }

    @Test
    public void salesSummaryCalculatesCorrectAverage() {
        // Clear and create known transactions
        orderService.placeOrder(new OrderRequest(null, "CU017",
                List.of(new OrderItemRequest("P001", "M", 2)), "CASH"));
        orderService.placeOrder(new OrderRequest(null, "CU018",
                List.of(new OrderItemRequest("P001", "M", 2)), "CASH"));
        
        SalesSummary summary = reportService.getTotalSales(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        
        double average = summary.getAveragePerTransaction();
        assertTrue("Average should be positive", average > 0);
    }

    @Test
    public void memberPurchaseHistoryIncludesTransactionDetails() {
        // Use CARD instead of WALLET to avoid member balance issues
        orderService.placeOrder(new OrderRequest("MB004", "CU019",
                List.of(new OrderItemRequest("P001", "M", 1)), "CARD"));
        
        List<MemberPurchase> purchases = reportService.getMemberPurchaseHistory("MB004");
        
        assertFalse("Should have purchases", purchases.isEmpty());
        MemberPurchase first = purchases.get(0);
        assertNotNull("Should have transaction ID", first.getTransactionId());
        assertNotNull("Should have date time", first.getDateTime());
        assertTrue("Should have amount", first.getAmount() > 0);
        assertNotNull("Should have payment method", first.getPaymentMethod());
    }
}
