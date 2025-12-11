package oopassignment.service;

import java.util.List;
import oopassignment.domain.product.StockItem;
import oopassignment.exception.InsufficientStockException;
import oopassignment.repository.StockRepository;
import oopassignment.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
    private final StockRepository stockRepository;

    public InventoryService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public boolean isStockAvailable(String productId, String size, int requestedQty) {
        if (requestedQty < 0) {
            return false;
        }
        // Normalize to uppercase for case-insensitive comparison
        String normalizedProductId = productId != null ? productId.toUpperCase().trim() : null;
        String normalizedSize = size != null ? size.toUpperCase().trim() : null;
        int available = stockRepository.getQuantity(normalizedProductId, normalizedSize);
        return available >= requestedQty;
    }

    public void reduceStock(String productId, String size, int qty) {
        ValidationUtils.validateQuantity(qty);
        // Normalize to uppercase for case-insensitive comparison
        String normalizedProductId = productId != null ? productId.toUpperCase().trim() : null;
        String normalizedSize = size != null ? size.toUpperCase().trim() : null;
        if (!isStockAvailable(normalizedProductId, normalizedSize, qty)) {
            throw new InsufficientStockException("Insufficient stock for " + productId + " (" + size + ")");
        }
        stockRepository.decreaseQuantity(normalizedProductId, normalizedSize, qty);
        LOG.info("Reduced stock {} {} by {}", normalizedProductId, normalizedSize, qty);
    }

    public void increaseStock(String productId, String size, int qty) {
        ValidationUtils.validateQuantity(qty);
        // Normalize to uppercase for case-insensitive comparison
        String normalizedProductId = productId != null ? productId.toUpperCase().trim() : null;
        String normalizedSize = size != null ? size.toUpperCase().trim() : null;
        stockRepository.increaseQuantity(normalizedProductId, normalizedSize, qty);
        LOG.info("Increased stock {} {} by {}", normalizedProductId, normalizedSize, qty);
    }

    public List<StockItem> getStockForProduct(String productId) {
        // Normalize productId to uppercase for case-insensitive lookup
        String normalizedProductId = productId != null ? productId.toUpperCase().trim() : null;
        return stockRepository.findByProductId(normalizedProductId);
    }
}
