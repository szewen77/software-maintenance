package oopassignment.service;

import java.util.List;
import java.util.Optional;
import oopassignment.config.AppConfig;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.ProductStatus;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.exception.InsufficientStockException;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public ProductService(ProductRepository productRepository, StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    public ProductRecord addProduct(String name, String category, double price) {
        String cleanName = ValidationUtils.requireNotBlank(name, "Name");
        String normalizedCategory = ValidationUtils.validateCategory(category, AppConfig.ALLOWED_PRODUCT_CATEGORIES);
        ValidationUtils.requirePositive(price, "Price");
        String id = generateId();
        ProductRecord product = new ProductRecord(id, cleanName, normalizedCategory, price, ProductStatus.ACTIVE);
        productRepository.save(product);
        LOG.info("Added product {} ({})", id, normalizedCategory);
        return product;
    }

    public void deleteProduct(String productId) {
        ProductRecord existing = getProductOrThrow(productId);
        if (!canDelete(existing.getProductId())) {
            throw new InsufficientStockException("Cannot delete product with available stock");
        }
        productRepository.delete(existing.getProductId());
        LOG.warn("Deleted product {}", existing.getProductId());
    }

    public boolean canDelete(String productId) {
        return stockRepository.getTotalQuantity(productId) == 0;
    }

    public Optional<ProductRecord> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        String normalized = id.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        // normalize to uppercase so P001 and p001 both work
        normalized = normalized.toUpperCase();
        return productRepository.findById(normalized);
    }

    public List<ProductRecord> findAll() {
        return productRepository.findAll();
    }

    private String generateId() {
        int max = 0;
        for (ProductRecord product : productRepository.findAll()) {
            String pid = product.getProductId();
            if (pid != null && pid.startsWith("P")) {
                try {
                    int num = Integer.parseInt(pid.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignore) {
                    // skip malformed ids
                }
            }
        }
        return "P" + String.format("%03d", max + 1);
    }

    private ProductRecord getProductOrThrow(String productId) {
        return findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }
}
