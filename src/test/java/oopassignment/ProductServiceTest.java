package oopassignment;

import java.util.List;
import oopassignment.domain.product.ProductRecord;
import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.service.ProductService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        productRepository = new InMemoryProductRepository();
        StockRepository stockRepository = new InMemoryStockRepository();
        productService = new ProductService(productRepository, stockRepository);
    }

    @Test
    public void cannotDeleteProductWithStock() {
        ProductRecord existing = productRepository.findById("P001").orElseThrow();
        assertThrows("Should not delete product while stock exists",
                InsufficientStockException.class,
                () -> productService.deleteProduct(existing.getProductId()));
    }

    @Test
    public void addProductGeneratesId() {
        ProductRecord added = productService.addProduct("Cap", "clothes", 25.0);
        assertNotNull("Product ID should be generated", added.getProductId());
    }

    @Test
    public void rejectInvalidCategory() {
        assertThrows("Invalid category should be rejected",
                InvalidInputException.class,
                () -> productService.addProduct("Weird", "invalidCat", 10.0));
    }

    @Test
    public void findByIdReturnsProduct() {
        var found = productService.findById("P001");
        assertTrue("Should find existing product", found.isPresent());
        assertEquals("P001", found.get().getProductId());
    }

    @Test
    public void findByIdReturnsEmptyForNonExistent() {
        var found = productService.findById("PXXX");
        assertFalse("Should return empty for non-existent", found.isPresent());
    }

    @Test
    public void findAllReturnsProducts() {
        List<ProductRecord> all = productService.findAll();
        assertNotNull("Should return list", all);
        assertFalse("Should have seeded products", all.isEmpty());
    }

    @Test
    public void findByIdNormalizesCase() {
        ProductRecord product = productService.addProduct("Case Test", "clothes", 25.0);
        
        var found = productService.findById(product.getProductId().toLowerCase());
        assertTrue("Should find product regardless of case", found.isPresent());
    }

    @Test
    public void findByIdTrimsWhitespace() {
        ProductRecord product = productService.addProduct("Trim Test", "shoes", 30.0);
        
        var found = productService.findById("  " + product.getProductId() + "  ");
        assertTrue("Should find product with whitespace", found.isPresent());
    }

    @Test
    public void canDeleteReturnsTrueForNoStock() {
        ProductRecord product = productService.addProduct("No Stock", "clothes", 20.0);
        
        boolean canDelete = productService.canDelete(product.getProductId());
        assertTrue("Should be able to delete product with no stock", canDelete);
    }

    @Test
    public void canDeleteReturnsFalseForExistingStock() {
        // P001 has stock from seed data
        boolean canDelete = productService.canDelete("P001");
        assertFalse("Should not be able to delete product with stock", canDelete);
    }

    @Test
    public void findByIdReturnsEmptyForNull() {
        var found = productService.findById(null);
        assertFalse("Should return empty for null", found.isPresent());
    }

    @Test
    public void findByIdReturnsEmptyForBlank() {
        var found = productService.findById("   ");
        assertFalse("Should return empty for blank", found.isPresent());
    }

    @Test
    public void addProductRejectsNegativePrice() {
        assertThrows("Should reject negative price",
                InvalidInputException.class,
                () -> productService.addProduct("Negative", "clothes", -5.0));
    }

    @Test
    public void addProductRejectsBlankName() {
        assertThrows("Should reject blank name",
                InvalidInputException.class,
                () -> productService.addProduct("", "clothes", 10.0));
    }
}
