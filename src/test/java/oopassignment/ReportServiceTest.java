package oopassignment;

import java.time.LocalDate;
import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import oopassignment.service.InventoryService;
import oopassignment.service.OrderService;
import oopassignment.service.PricingService;
import oopassignment.service.ReportService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReportServiceTest {

    private ReportService reportService;
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
        reportService = new ReportService(transactionRepository);
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
}
