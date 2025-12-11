package oopassignment.domain.order;

import java.time.LocalDateTime;

public class TransactionHeader {
    private final String transactionId;
    private final LocalDateTime dateTime;
    private final String memberId;
    private final String customerId;
    private final double totalAmount;
    private final String paymentMethod;

    public TransactionHeader(String transactionId, LocalDateTime dateTime, String memberId, String customerId,
                             double totalAmount, String paymentMethod) {
        this.transactionId = transactionId;
        this.dateTime = dateTime;
        this.memberId = memberId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
