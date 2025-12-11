package oopassignment.domain.report;

import java.time.LocalDateTime;

public class MemberPurchase {
    private final String transactionId;
    private final LocalDateTime dateTime;
    private final double amount;
    private final String paymentMethod;

    public MemberPurchase(String transactionId, LocalDateTime dateTime, double amount, String paymentMethod) {
        this.transactionId = transactionId;
        this.dateTime = dateTime;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
