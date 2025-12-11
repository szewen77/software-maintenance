package oopassignment.domain.order;

public class OrderResult {
    private final String transactionId;
    private final double subtotal;
    private final double discount;
    private final double total;
    private final boolean memberDiscountApplied;

    public OrderResult(String transactionId, double subtotal, double discount, double total, boolean memberDiscountApplied) {
        this.transactionId = transactionId;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.memberDiscountApplied = memberDiscountApplied;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public boolean isMemberDiscountApplied() {
        return memberDiscountApplied;
    }
}
