package oopassignment;

import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.domain.auth.EmploymentStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AuthResultTest {

    @Test
    public void successfulAuthResultCreation() {
        EmployeeRecord employee = new EmployeeRecord("E001", "manager", "hash", 
                Role.MANAGER, 5000.0, 0.20, null, EmploymentStatus.ACTIVE);
        
        AuthResult result = new AuthResult(true, false, "Login successful", employee);
        
        assertTrue("Should be successful", result.isSuccess());
        assertFalse("Should not be locked", result.isLocked());
        assertEquals("Login successful", result.getMessage());
        assertNotNull("User should not be null", result.getUser());
        assertEquals("E001", result.getUser().getId());
    }

    @Test
    public void failedAuthResultCreation() {
        AuthResult result = new AuthResult(false, false, "Invalid credentials", null);
        
        assertFalse("Should not be successful", result.isSuccess());
        assertFalse("Should not be locked", result.isLocked());
        assertEquals("Invalid credentials", result.getMessage());
        assertNull("User should be null", result.getUser());
    }

    @Test
    public void lockedAuthResultCreation() {
        AuthResult result = new AuthResult(false, true, "Account locked", null);
        
        assertFalse("Should not be successful", result.isSuccess());
        assertTrue("Should be locked", result.isLocked());
        assertEquals("Account locked", result.getMessage());
        assertNull("User should be null", result.getUser());
    }

    @Test
    public void authResultWithAllFields() {
        EmployeeRecord employee = new EmployeeRecord("E002", "staff", "hash", 
                Role.STAFF, 3000.0, 0.10, "M001", EmploymentStatus.ACTIVE);
        
        AuthResult result = new AuthResult(true, false, "Welcome", employee);
        
        assertTrue(result.isSuccess());
        assertFalse(result.isLocked());
        assertEquals("Welcome", result.getMessage());
        assertEquals("E002", result.getUser().getId());
        assertEquals("staff", result.getUser().getUsername());
    }

    @Test
    public void authResultGetters() {
        EmployeeRecord employee = new EmployeeRecord("E003", "user", "hash", 
                Role.STAFF, 2500.0, 0.10, null, EmploymentStatus.ACTIVE);
        
        AuthResult result = new AuthResult(true, false, "Test message", employee);
        
        assertEquals(true, result.isSuccess());
        assertEquals(false, result.isLocked());
        assertEquals("Test message", result.getMessage());
        assertEquals(employee, result.getUser());
    }
}

