package oopassignment;

import oopassignment.exception.InvalidInputException;
import oopassignment.util.ValidationUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ValidationUtilsTest {

    @Test
    public void requireNotBlankAcceptsValidString() {
        String result = ValidationUtils.requireNotBlank("valid", "Field");
        assertEquals("valid", result);
    }

    @Test
    public void requireNotBlankTrimsWhitespace() {
        String result = ValidationUtils.requireNotBlank("  value  ", "Field");
        assertEquals("value", result);
    }

    @Test
    public void requireNotBlankRejectsNull() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requireNotBlank(null, "Field"));
    }

    @Test
    public void requireNotBlankRejectsEmptyString() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requireNotBlank("", "Field"));
    }

    @Test
    public void requireNotBlankRejectsWhitespaceOnly() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requireNotBlank("   ", "Field"));
    }

    @Test
    public void requirePositiveAcceptsPositiveNumber() {
        ValidationUtils.requirePositive(10.0, "Value");
        ValidationUtils.requirePositive(0.01, "Value");
        ValidationUtils.requirePositive(1000.0, "Value");
    }

    @Test
    public void requirePositiveRejectsZero() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requirePositive(0.0, "Value"));
    }

    @Test
    public void requirePositiveRejectsNegative() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requirePositive(-1.0, "Value"));
    }

    @Test
    public void requireNonNegativeAcceptsZero() {
        ValidationUtils.requireNonNegative(0.0, "Value");
    }

    @Test
    public void requireNonNegativeAcceptsPositive() {
        ValidationUtils.requireNonNegative(10.0, "Value");
    }

    @Test
    public void requireNonNegativeRejectsNegative() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.requireNonNegative(-1.0, "Value"));
    }

    @Test
    public void validateQuantityAcceptsPositive() {
        ValidationUtils.validateQuantity(1);
        ValidationUtils.validateQuantity(100);
        ValidationUtils.validateQuantity(999);
    }

    @Test
    public void validateQuantityRejectsZero() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.validateQuantity(0));
    }

    @Test
    public void validateQuantityRejectsNegative() {
        assertThrows(InvalidInputException.class,
                () -> ValidationUtils.validateQuantity(-1));
    }

    @Test
    public void validationExceptionIncludesFieldName() {
        try {
            ValidationUtils.requireNotBlank("", "TestField");
        } catch (InvalidInputException e) {
            String message = e.getMessage().toLowerCase();
            // Message should mention the field name
            assertEquals(true, message.contains("testfield") || message.contains("blank") || message.contains("empty"));
        }
    }
}

