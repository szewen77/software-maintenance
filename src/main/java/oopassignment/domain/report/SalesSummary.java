package oopassignment.domain.report;

public class SalesSummary {
    private final double totalAmount;
    private final int transactionCount;

    public SalesSummary(double totalAmount, int transactionCount) {
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public double getAveragePerTransaction() {
        if (transactionCount == 0) {
            return 0.0;
        }
        return totalAmount / transactionCount;
    }
}
