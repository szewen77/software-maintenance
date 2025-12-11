package oopassignment;

import oopassignment.service.PricingService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PricingServiceTest {

    private PricingService pricingService;

    @Before
    public void setUp() {
        pricingService = new PricingService();
    }

    @Test
    public void memberDiscountRateIs5Percent() {
        double rate = pricingService.getMemberDiscountRate();
        assertEquals("Member discount rate should be 5%", 0.05, rate, 0.0001);
    }

    @Test
    public void memberDiscountAppliesCorrectly() {
        double subtotal = 100.0;
        double discounted = pricingService.applyMemberDiscount(subtotal, true);
        assertEquals("Member should get 5% discount", 95.0, discounted, 0.0001);
    }

    @Test
    public void nonMemberGetsNoDiscount() {
        double subtotal = 100.0;
        double result = pricingService.applyMemberDiscount(subtotal, false);
        assertEquals("Non-member should get no discount", 100.0, result, 0.0001);
    }

    @Test
    public void memberDiscountOnSmallAmount() {
        double subtotal = 10.0;
        double discounted = pricingService.applyMemberDiscount(subtotal, true);
        assertEquals("Discount should apply to small amounts", 9.50, discounted, 0.0001);
    }

    @Test
    public void memberDiscountOnLargeAmount() {
        double subtotal = 1000.0;
        double discounted = pricingService.applyMemberDiscount(subtotal, true);
        assertEquals("Discount should apply to large amounts", 950.0, discounted, 0.0001);
    }

    @Test
    public void zeroSubtotalRemainsZero() {
        double result = pricingService.applyMemberDiscount(0, true);
        assertEquals("Zero subtotal should remain zero", 0.0, result, 0.0001);
    }

    @Test
    public void memberDiscountCalculation() {
        double subtotal = 19.90;
        double discounted = pricingService.applyMemberDiscount(subtotal, true);
        double expected = 19.90 * 0.95;
        assertEquals("Discount calculation should be accurate", expected, discounted, 0.0001);
    }

    @Test
    public void multipleItemsWithMemberDiscount() {
        double subtotal = 19.90 + 120.00;
        double discounted = pricingService.applyMemberDiscount(subtotal, true);
        double expected = (19.90 + 120.00) * 0.95;
        assertEquals("Discount should apply to total", expected, discounted, 0.01);
    }
}

