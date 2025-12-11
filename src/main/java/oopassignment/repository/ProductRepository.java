package oopassignment.repository;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.product.ProductRecord;

public interface ProductRepository {
    List<ProductRecord> findAll();

    Optional<ProductRecord> findById(String id);

    void save(ProductRecord product);

    void update(ProductRecord product);

    void delete(String productId);
}
