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
}
