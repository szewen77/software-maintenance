package oopassignment.service;

import oopassignment.config.AppConfig;

public class PricingService {
    private static final double MEMBER_DISCOUNT_RATE = AppConfig.MEMBER_DISCOUNT_RATE;

    public double applyMemberDiscount(double amount, boolean isMember) {
        if (!isMember) {
            return amount;
        }
        return amount * (1 - MEMBER_DISCOUNT_RATE);
    }

    public double getMemberDiscountRate() {
        return MEMBER_DISCOUNT_RATE;
    }
}
