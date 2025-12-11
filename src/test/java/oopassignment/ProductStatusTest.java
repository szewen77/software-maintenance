package oopassignment;

import oopassignment.domain.product.ProductStatus;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for ProductStatus enum to ensure 100% coverage
 */
public class ProductStatusTest {

    @Test
    public void productStatusHasActiveValue() {
        ProductStatus status = ProductStatus.ACTIVE;
        assertNotNull("ACTIVE should exist", status);
        assertEquals("ACTIVE", status.name());
    }

    @Test
    public void productStatusHasInactiveValue() {
        ProductStatus status = ProductStatus.INACTIVE;
        assertNotNull("INACTIVE should exist", status);
        assertEquals("INACTIVE", status.name());
    }

    @Test
    public void productStatusValues() {
        ProductStatus[] values = ProductStatus.values();
        assertEquals("Should have 2 values", 2, values.length);
        assertTrue("Should contain ACTIVE", containsStatus(values, ProductStatus.ACTIVE));
        assertTrue("Should contain INACTIVE", containsStatus(values, ProductStatus.INACTIVE));
    }

    @Test
    public void productStatusValueOf() {
        assertEquals(ProductStatus.ACTIVE, ProductStatus.valueOf("ACTIVE"));
        assertEquals(ProductStatus.INACTIVE, ProductStatus.valueOf("INACTIVE"));
    }

    @Test
    public void productStatusToString() {
        assertEquals("ACTIVE", ProductStatus.ACTIVE.toString());
        assertEquals("INACTIVE", ProductStatus.INACTIVE.toString());
    }

    @Test
    public void productStatusEquality() {
        ProductStatus active1 = ProductStatus.ACTIVE;
        ProductStatus active2 = ProductStatus.valueOf("ACTIVE");
        ProductStatus inactive = ProductStatus.INACTIVE;
        
        assertTrue("Same status should be equal", active1 == active2);
        assertFalse("Different statuses should not be equal", active1 == inactive);
    }

    @Test
    public void productStatusOrdinal() {
        assertEquals(0, ProductStatus.ACTIVE.ordinal());
        assertEquals(1, ProductStatus.INACTIVE.ordinal());
    }

    @Test
    public void productStatusComparison() {
        assertTrue("ACTIVE should come before INACTIVE", 
                ProductStatus.ACTIVE.ordinal() < ProductStatus.INACTIVE.ordinal());
    }

    private boolean containsStatus(ProductStatus[] values, ProductStatus status) {
        for (ProductStatus value : values) {
            if (value == status) {
                return true;
            }
        }
        return false;
    }
}

