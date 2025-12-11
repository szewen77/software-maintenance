package oopassignment.util;

import java.util.List;
import oopassignment.domain.auth.Role;
import oopassignment.exception.InvalidInputException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static String requireNotBlank(String value, String fieldName) {
        if (value == null) {
            throw new InvalidInputException(fieldName + " cannot be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be blank");
        }
        return trimmed;
    }

    public static double requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than 0");
        }
        return value;
    }

    public static double requireNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new InvalidInputException(fieldName + " cannot be negative");
        }
        return value;
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than 0");
        }
        return value;
    }

    public static void validateSalary(double baseSalary) {
        requirePositive(baseSalary, "Base salary");
    }

    public static void validateQuantity(int quantity) {
        requirePositive(quantity, "Quantity");
    }

    public static String validateCategory(String category, List<String> allowedCategories) {
        String normalized = requireNotBlank(category, "Category").toLowerCase();
        if (!allowedCategories.contains(normalized)) {
            throw new InvalidInputException("Category must be one of: " + String.join(", ", allowedCategories));
        }
        return normalized;
    }

    /**
     * Normalises upline rule while keeping staff flexible (they can be self-serve or auto-assigned).
     */
    public static String validateRoleTransition(Role role, String uplineId) {
        if (role == Role.MANAGER) {
            return null;
        }
        return uplineId == null ? null : uplineId.trim();
    }
}
