package oopassignment.domain.order;

public class OrderItemRequest {
    private final String productId;
    private final String size;
    private final int quantity;

    public OrderItemRequest(String productId, String size, int quantity) {
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
}
