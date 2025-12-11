package oopassignment;

import oopassignment.domain.member.MemberRecord;
import oopassignment.domain.product.ProductRecord;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.service.InventoryService;
import oopassignment.service.MemberService;
import oopassignment.service.ProductService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests specifically targeting service layer branches
 */
public class ServiceBranchTest {

    private MemberService memberService;
    private ProductService productService;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        memberService = new MemberService(memberRepository);
        
        ProductRepository productRepository = new InMemoryProductRepository();
        StockRepository stockRepository = new InMemoryStockRepository();
        productService = new ProductService(productRepository, stockRepository);
        inventoryService = new InventoryService(stockRepository);
    }

    // Member Service Branches
    @Test
    public void memberServiceFindByIdWithNullReturnsEmpty() {
        var result = memberService.findById(null);
        assertFalse(result.isPresent());
    }

    @Test
    public void memberServiceFindByIdWithBlankReturnsEmpty() {
        var result = memberService.findById("   ");
        assertFalse(result.isPresent());
    }

    @Test
    public void memberServiceFindByIdWithValidId() {
        MemberRecord member = memberService.registerMember("Test", "900101011234", 100.0);
        var result = memberService.findById(member.getMemberId());
        assertTrue(result.isPresent());
    }

    @Test
    public void memberServiceIsValidIcWithNull() {
        assertFalse(memberService.isValidIc(null));
    }

    @Test
    public void memberServiceIsValidIcWithValidFormat() {
        assertTrue(memberService.isValidIc("900101011234"));
        assertTrue(memberService.isValidIc("900101-01-1234"));
    }

    @Test
    public void memberServiceIsValidIcWithInvalidFormat() {
        assertFalse(memberService.isValidIc("123"));
        assertFalse(memberService.isValidIc("ABC"));
    }

    @Test
    public void memberServiceMaskIcShortString() {
        String masked = memberService.maskIc("123");
        assertEquals("****", masked);
    }

    @Test
    public void memberServiceMaskIcNormalString() {
        String masked = memberService.maskIc("900101011234");
        assertTrue(masked.contains("****"));
    }

    @Test
    public void memberServiceTopUpWithZeroThrows() {
        MemberRecord member = memberService.registerMember("Test", "910101011234", 0);
        assertThrows(InvalidInputException.class,
                () -> memberService.topUpCredit(member.getMemberId(), 0));
    }

    @Test
    public void memberServiceDeductWithZeroThrows() {
        MemberRecord member = memberService.registerMember("Test", "920101011234", 100.0);
        assertThrows(InvalidInputException.class,
                () -> memberService.deductCredit(member.getMemberId(), 0));
    }

    @Test
    public void memberServiceDeductExactBalance() {
        MemberRecord member = memberService.registerMember("Test", "930101011234", 50.0);
        memberService.deductCredit(member.getMemberId(), 50.0);
        
        MemberRecord updated = memberService.findById(member.getMemberId()).orElseThrow();
        assertEquals(0.0, updated.getCreditBalance(), 0.001);
    }

    // Product Service Branches
    @Test
    public void productServiceFindByIdWithNull() {
        var result = productService.findById(null);
        assertFalse(result.isPresent());
    }

    @Test
    public void productServiceFindByIdWithEmptyString() {
        var result = productService.findById("");
        assertFalse(result.isPresent());
    }

    @Test
    public void productServiceFindByIdWithWhitespace() {
        var result = productService.findById("   ");
        assertFalse(result.isPresent());
    }

    @Test
    public void productServiceCanDeleteWithStock() {
        // P001 has stock
        boolean canDelete = productService.canDelete("P001");
        assertFalse(canDelete);
    }

    @Test
    public void productServiceCanDeleteWithoutStock() {
        ProductRecord product = productService.addProduct("No Stock Product", "clothes", 25.0);
        boolean canDelete = productService.canDelete(product.getProductId());
        assertTrue(canDelete);
    }

    @Test
    public void productServiceDeleteWithStockThrows() {
        assertThrows(Exception.class,
                () -> productService.deleteProduct("P001"));
    }

    @Test
    public void productServiceAddWithValidCategory() {
        ProductRecord product = productService.addProduct("Valid", "clothes", 20.0);
        assertNotNull(product);
        assertEquals("clothes", product.getCategory());
    }

    @Test
    public void productServiceAddWithShoesCategory() {
        ProductRecord product = productService.addProduct("Shoes Product", "shoes", 100.0);
        assertNotNull(product);
        assertEquals("shoes", product.getCategory());
    }

    // Inventory Service Branches
    @Test
    public void inventoryServiceIsStockAvailableWithZeroQuantity() {
        boolean available = inventoryService.isStockAvailable("P001", "M", 0);
        assertTrue(available);
    }

    @Test
    public void inventoryServiceIsStockAvailableWithNegativeQuantity() {
        boolean available = inventoryService.isStockAvailable("P001", "M", -1);
        assertFalse(available);
    }

    @Test
    public void inventoryServiceIsStockAvailableWithExactQuantity() {
        int total = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "M".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        boolean available = inventoryService.isStockAvailable("P001", "M", total);
        assertTrue(available);
    }

    @Test
    public void inventoryServiceIsStockAvailableExceedsStock() {
        int total = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "M".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        boolean available = inventoryService.isStockAvailable("P001", "M", total + 1);
        assertFalse(available);
    }

    @Test
    public void inventoryServiceReduceStockByOne() {
        int before = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "L".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        inventoryService.reduceStock("P001", "L", 1);
        
        int after = inventoryService.getStockForProduct("P001").stream()
                .filter(s -> "L".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        assertEquals(before - 1, after);
    }

    @Test
    public void inventoryServiceIncreaseStockByOne() {
        int before = inventoryService.getStockForProduct("P002").stream()
                .filter(s -> "42".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        inventoryService.increaseStock("P002", "42", 1);
        
        int after = inventoryService.getStockForProduct("P002").stream()
                .filter(s -> "42".equals(s.getSize()))
                .mapToInt(s -> s.getQuantity())
                .sum();
        
        assertEquals(before + 1, after);
    }

    @Test
    public void inventoryServiceGetStockForNonExistentProduct() {
        var stock = inventoryService.getStockForProduct("PXXX");
        assertTrue(stock.isEmpty());
    }
}

