package oopassignment;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

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
}
