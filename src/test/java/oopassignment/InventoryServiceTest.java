package oopassignment;

import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.StockRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.service.InventoryService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class InventoryServiceTest {

    private InventoryService inventoryService;

    @Before
    public void setUp() {
        StockRepository stockRepository = new InMemoryStockRepository();
        inventoryService = new InventoryService(stockRepository);
    }

    @Test
    public void stockAvailabilityIsCorrect() {
        boolean available = inventoryService.isStockAvailable("P001", "M", 5);
        assertTrue("Stock should be available for P001 size M qty 5", available);
    }

    @Test
    public void reduceStockLowersQuantity() {
        inventoryService.reduceStock("P001", "M", 2);
        boolean stillAvailable = inventoryService.isStockAvailable("P001", "M", 8);
        assertTrue("Quantity should update after reduction", stillAvailable);
    }

    @Test
    public void reduceMoreThanAvailableThrows() {
        assertThrows("Should reject reducing more than available",
                InsufficientStockException.class,
                () -> inventoryService.reduceStock("P001", "M", 100));
    }

    @Test
    public void negativeRequestedQuantityIsNotAvailable() {
        assertFalse("Negative quantity should not be allowed", inventoryService.isStockAvailable("P001", "M", -1));
    }

    @Test
    public void zeroOrNegativeReductionsAreInvalid() {
        assertThrows("Zero reductions should be rejected",
                InvalidInputException.class,
                () -> inventoryService.reduceStock("P001", "M", 0));
    }

    @Test
    public void increaseStockRaisesAvailableQuantity() {
        inventoryService.increaseStock("P001", "M", 5);
        assertTrue("Increased stock should allow larger orders", inventoryService.isStockAvailable("P001", "M", 14));
    }

    @Test
    public void increaseStockWithZeroQuantityThrows() {
        assertThrows("Zero increase should be rejected",
                InvalidInputException.class,
                () -> inventoryService.increaseStock("P001", "M", 0));
    }

    @Test
    public void increaseStockWithNegativeQuantityThrows() {
        assertThrows("Negative increase should be rejected",
                InvalidInputException.class,
                () -> inventoryService.increaseStock("P001", "M", -5));
    }

    @Test
    public void getStockForProductReturnsItems() {
        var stockItems = inventoryService.getStockForProduct("P001");
        assertFalse("Should return stock items for existing product", stockItems.isEmpty());
    }

    @Test
    public void getStockForNonExistentProductReturnsEmpty() {
        var stockItems = inventoryService.getStockForProduct("P999");
        assertTrue("Should return empty list for non-existent product", stockItems.isEmpty());
    }

    @Test
    public void multipleReductionsUpdateStock() {
        inventoryService.reduceStock("P001", "M", 1);
        inventoryService.reduceStock("P001", "M", 1);
        inventoryService.reduceStock("P001", "M", 1);
        
        assertTrue("Should still have stock after multiple reductions", 
                inventoryService.isStockAvailable("P001", "M", 5));
    }

    @Test
    public void differentSizesTrackedSeparately() {
        boolean sizeM = inventoryService.isStockAvailable("P001", "M", 5);
        boolean sizeL = inventoryService.isStockAvailable("P001", "L", 5);
        
        assertTrue("Size M should be available", sizeM);
        assertTrue("Size L should be available", sizeL);
    }

    @Test
    public void reduceStockAcrossDifferentSizes() {
        inventoryService.reduceStock("P001", "M", 2);
        inventoryService.reduceStock("P001", "L", 3);
        
        assertTrue("Size M should still have stock", inventoryService.isStockAvailable("P001", "M", 5));
        assertTrue("Size L should still have stock", inventoryService.isStockAvailable("P001", "L", 5));
    }

    @Test
    public void exactStockAmountIsAvailable() {
        int totalStock = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "M".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        assertTrue("Exact stock amount should be available", 
                inventoryService.isStockAvailable("P001", "M", totalStock));
    }

    @Test
    public void oneMoreThanStockIsNotAvailable() {
        int totalStock = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "M".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        assertFalse("One more than stock should not be available", 
                inventoryService.isStockAvailable("P001", "M", totalStock + 1));
    }
}
