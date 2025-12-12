package oopassignment;

import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.domain.member.MemberRecord;
import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.service.InventoryService;
import oopassignment.service.OrderService;
import oopassignment.service.PricingService;
import oopassignment.service.MemberService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class OrderServiceTest {

    private OrderService orderService;
    private PricingService pricingService;
    private MemberService memberService;
    private MemberRepository memberRepository;

    @Before
    public void setUp() {
        ProductRepository productRepository = new InMemoryProductRepository();
        StockRepository stockRepository = new InMemoryStockRepository();
        InventoryService inventoryService = new InventoryService(stockRepository);
        pricingService = new PricingService();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        memberRepository = new InMemoryMemberRepository();
        memberService = new MemberService(memberRepository);
        orderService = new OrderService(productRepository, inventoryService, pricingService, transactionRepository, memberRepository);
    }

    @Test
    public void memberDiscountApplied() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 2);
        OrderRequest request = new OrderRequest("MB001", List.of(item), "CASH");
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
        OrderRequest request = new OrderRequest(null, List.of(item), "CARD");
        OrderResult result = orderService.placeOrder(request);

        assertFalse("No discount for non-member", result.isMemberDiscountApplied());
        assertEquals("Total should equal unit price", 19.90, result.getTotal(), 0.0001);
    }

    @Test
    public void insufficientStockThrows() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1000);
        OrderRequest request = new OrderRequest(null, List.of(item), "CASH");
        assertThrows("Should fail when stock is insufficient",
                InsufficientStockException.class,
                () -> orderService.placeOrder(request));
    }

    @Test
    public void mixedItemsTotalMatches() {
        OrderItemRequest item1 = new OrderItemRequest("P001", "M", 1);
        OrderItemRequest item2 = new OrderItemRequest("P002", "42", 1);
        OrderRequest request = new OrderRequest(null, List.of(item1, item2), "CASH");
        OrderResult result = orderService.placeOrder(request);

        double expectedTotal = 19.90 + 120.00;
        assertEquals("Total should sum mixed items", expectedTotal, result.getTotal(), 0.0001);
    }

    @Test
    public void emptyOrderRejected() {
        OrderRequest empty = new OrderRequest(null, List.of(), "CASH");
        assertThrows("Empty orders should be rejected",
                InvalidInputException.class,
                () -> orderService.placeOrder(empty));
    }

    @Test
    public void walletPaymentDeductsBalance() {
        MemberRecord member = memberService.registerMember("Wallet User", "950101-01-1234", 100.0);
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1);
        OrderRequest request = new OrderRequest(member.getMemberId(), List.of(item), "WALLET");
        
        OrderResult result = orderService.placeOrder(request);
        
        assertTrue("Member discount should apply", result.isMemberDiscountApplied());
        MemberRecord updated = memberService.findById(member.getMemberId()).orElseThrow();
        double expectedBalance = 100.0 - result.getTotal();
        assertEquals("Wallet balance should be deducted", expectedBalance, updated.getCreditBalance(), 0.0001);
    }

    @Test
    public void walletPaymentWithInsufficientBalanceThrows() {
        MemberRecord member = memberService.registerMember("Poor User", "960202-02-5678", 5.0);
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1);
        OrderRequest request = new OrderRequest(member.getMemberId(), List.of(item), "WALLET");
        
        assertThrows("Should reject wallet payment with insufficient balance",
                InvalidInputException.class,
                () -> orderService.placeOrder(request));
    }

    @Test
    public void walletPaymentForNonMemberThrows() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1);
        OrderRequest request = new OrderRequest(null, List.of(item), "WALLET");
        
        assertThrows("Should reject wallet payment for non-members",
                InvalidInputException.class,
                () -> orderService.placeOrder(request));
    }

    @Test
    public void walletPaymentWithInvalidMemberThrows() {
        OrderItemRequest item = new OrderItemRequest("P001", "M", 1);
        OrderRequest request = new OrderRequest("MB999", List.of(item), "WALLET");
        
        assertThrows("Should reject wallet payment with invalid member",
                EntityNotFoundException.class,
                () -> orderService.placeOrder(request));
    }

    @Test
    public void walletPaymentWithMultipleItems() {
        MemberRecord member = memberService.registerMember("Big Spender", "970303-03-9876", 200.0);
        OrderItemRequest item1 = new OrderItemRequest("P001", "M", 2);
        OrderItemRequest item2 = new OrderItemRequest("P002", "42", 1);
        OrderRequest request = new OrderRequest(member.getMemberId(), List.of(item1, item2), "WALLET");
        
        OrderResult result = orderService.placeOrder(request);
        
        MemberRecord updated = memberService.findById(member.getMemberId()).orElseThrow();
        double expectedBalance = 200.0 - result.getTotal();
        assertEquals("Wallet should handle multiple items", expectedBalance, updated.getCreditBalance(), 0.01);
    }
}
