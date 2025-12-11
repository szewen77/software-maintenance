package oopassignment.repository;

import java.util.List;
import oopassignment.domain.product.StockItem;

public interface StockRepository {
    List<StockItem> findByProductId(String productId);

    int getQuantity(String productId, String size);

    int getTotalQuantity(String productId);

    void setQuantity(String productId, String size, int qty);

    void increaseQuantity(String productId, String size, int qty);

    void decreaseQuantity(String productId, String size, int qty);
}
