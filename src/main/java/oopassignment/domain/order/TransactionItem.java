package oopassignment.domain.order;

public class TransactionItem {
    private final String transactionId;
    private final int lineNo;
    private final String productId;
    private final String size;
    private final int quantity;
    private final double unitPrice;

    public TransactionItem(String transactionId, int lineNo, String productId, String size, int quantity, double unitPrice) {
        this.transactionId = transactionId;
        this.lineNo = lineNo;
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getProductId() {
        return productId;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
