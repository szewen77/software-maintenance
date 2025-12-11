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
        int available = stockRepository.getQuantity(productId, size);
        return available >= requestedQty;
    }

    public void reduceStock(String productId, String size, int qty) {
        ValidationUtils.validateQuantity(qty);
        if (!isStockAvailable(productId, size, qty)) {
            throw new InsufficientStockException("Insufficient stock for " + productId + " (" + size + ")");
        }
        stockRepository.decreaseQuantity(productId, size, qty);
        LOG.info("Reduced stock {} {} by {}", productId, size, qty);
    }

    public void increaseStock(String productId, String size, int qty) {
        ValidationUtils.validateQuantity(qty);
        stockRepository.increaseQuantity(productId, size, qty);
        LOG.info("Increased stock {} {} by {}", productId, size, qty);
    }

    public List<StockItem> getStockForProduct(String productId) {
        return stockRepository.findByProductId(productId);
    }
}
