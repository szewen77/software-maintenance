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
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, ProductRecord> products = new LinkedHashMap<>();

    public InMemoryProductRepository() {
        seedDefaults();
    }

    private void seedDefaults() {
        save(new ProductRecord("P001", "Basic Tee", "Clothes", 19.90, ProductStatus.ACTIVE));
        save(new ProductRecord("P002", "Running Shoe", "Shoes", 120.00, ProductStatus.ACTIVE));
    }

    @Override
    public List<ProductRecord> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Optional<ProductRecord> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public void save(ProductRecord product) {
        products.put(product.getProductId(), product);
    }

    @Override
    public void update(ProductRecord product) {
        products.put(product.getProductId(), product);
    }

    @Override
    public void delete(String productId) {
        products.remove(productId);
    }
}
