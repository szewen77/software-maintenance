package oopassignment.domain.product;

public class StockItem {
    private final String productId;
    private final String size;
    private int quantity;

    public StockItem(String productId, String size, int quantity) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
