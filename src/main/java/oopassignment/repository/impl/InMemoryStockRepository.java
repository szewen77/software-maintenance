package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStockRepository implements StockRepository {

    private final Map<String, StockItem> stock = new LinkedHashMap<>();

    public InMemoryStockRepository() {
        seedDefaults();
    }

    private void seedDefaults() {
        setQuantity("P001", "M", 10);
        setQuantity("P001", "L", 8);
        setQuantity("P002", "42", 5);
    }

    @Override
    public List<StockItem> findByProductId(String productId) {
        List<StockItem> items = new ArrayList<>();
        for (StockItem item : stock.values()) {
            if (item.getProductId().equals(productId)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public int getQuantity(String productId, String size) {
        return stock.getOrDefault(key(productId, size), new StockItem(productId, size, 0)).getQuantity();
    }

    @Override
    public int getTotalQuantity(String productId) {
        int total = 0;
        for (StockItem item : findByProductId(productId)) {
            total += item.getQuantity();
        }
        return total;
    }

    @Override
    public void setQuantity(String productId, String size, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        stock.put(key(productId, size), new StockItem(productId, size, qty));
    }

    @Override
    public void increaseQuantity(String productId, String size, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        int current = getQuantity(productId, size);
        setQuantity(productId, size, current + qty);
    }

    @Override
    public void decreaseQuantity(String productId, String size, int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        int current = getQuantity(productId, size);
        if (qty > current) {
            throw new IllegalStateException("Not enough stock to reduce");
        }
        setQuantity(productId, size, current - qty);
    }

    private String key(String productId, String size) {
        return productId + "|" + size;
    }
}
