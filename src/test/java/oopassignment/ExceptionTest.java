package oopassignment;

import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.exception.UnauthorizedActionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExceptionTest {

    @Test
    public void duplicateEntityExceptionWithMessage() {
        DuplicateEntityException ex = new DuplicateEntityException("Duplicate found");
        assertEquals("Duplicate found", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    public void duplicateEntityExceptionCanBeThrown() {
        try {
            throw new DuplicateEntityException("Test duplicate");
        } catch (DuplicateEntityException e) {
            assertEquals("Test duplicate", e.getMessage());
        }
    }

    @Test
    public void entityNotFoundExceptionWithMessage() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        assertEquals("Entity not found", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    public void entityNotFoundExceptionCanBeThrown() {
        try {
            throw new EntityNotFoundException("Test not found");
        } catch (EntityNotFoundException e) {
            assertEquals("Test not found", e.getMessage());
        }
    }

    @Test
    public void insufficientStockExceptionWithMessage() {
        InsufficientStockException ex = new InsufficientStockException("Not enough stock");
        assertEquals("Not enough stock", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    public void insufficientStockExceptionCanBeThrown() {
        try {
            throw new InsufficientStockException("Test stock issue");
        } catch (InsufficientStockException e) {
            assertEquals("Test stock issue", e.getMessage());
        }
    }

    @Test
    public void invalidInputExceptionWithMessage() {
        InvalidInputException ex = new InvalidInputException("Invalid input");
        assertEquals("Invalid input", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    public void invalidInputExceptionCanBeThrown() {
        try {
            throw new InvalidInputException("Test invalid");
        } catch (InvalidInputException e) {
            assertEquals("Test invalid", e.getMessage());
        }
    }

    @Test
    public void unauthorizedActionExceptionWithMessage() {
        UnauthorizedActionException ex = new UnauthorizedActionException("Unauthorized");
        assertEquals("Unauthorized", ex.getMessage());
        assertNotNull(ex);
    }

    @Test
    public void unauthorizedActionExceptionCanBeThrown() {
        try {
            throw new UnauthorizedActionException("Test unauthorized");
        } catch (UnauthorizedActionException e) {
            assertEquals("Test unauthorized", e.getMessage());
        }
    }

    @Test
    public void allExceptionsAreRuntimeExceptions() {
        Exception ex1 = new DuplicateEntityException("test");
        Exception ex2 = new EntityNotFoundException("test");
        Exception ex3 = new InsufficientStockException("test");
        Exception ex4 = new InvalidInputException("test");
        Exception ex5 = new UnauthorizedActionException("test");
        
        // All should be RuntimeExceptions
        assertNotNull(ex1);
        assertNotNull(ex2);
        assertNotNull(ex3);
        assertNotNull(ex4);
        assertNotNull(ex5);
    }
}

