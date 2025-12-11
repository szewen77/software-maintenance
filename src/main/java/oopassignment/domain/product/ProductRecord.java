package oopassignment.domain.product;

public class ProductRecord {
    private String productId;
    private String name;
    private String category;
    private double price;
    private ProductStatus status;

    public ProductRecord(String productId, String name, String category, double price, ProductStatus status) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
