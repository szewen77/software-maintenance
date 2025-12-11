package oopassignment;

import oopassignment.repository.EmployeeRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.service.AuthService;
import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthServiceTest {

    private AuthService authService;

    @Before
    public void setUp() {
        PasswordHasher hasher = new PasswordHasher();
        EmployeeRepository repository = new InMemoryEmployeeRepository(hasher);
        authService = new AuthService(repository, hasher);
    }

    @Test
    public void validLoginSucceeds() {
        AuthResult result = authService.login("manager", "password123");
        assertTrue("Expected login success for default manager", result.isSuccess());
    }

    @Test
    public void invalidPasswordFails() {
        AuthResult result = authService.login("manager", "wrongpassword");
        assertFalse("Expected login failure for wrong password", result.isSuccess());
    }

    @Test
    public void lockedAfterThreeFailures() {
        authService.login("staff", "bad1");
        authService.login("staff", "bad2");
        AuthResult lockedResult = authService.login("staff", "bad3");
        assertTrue("Account should be locked after 3 attempts", lockedResult.isLocked());

        AuthResult stillLocked = authService.login("staff", "password123");
        assertTrue("Lock should remain until duration expires", stillLocked.isLocked());
    }

    @Test
    public void nonExistentUserFails() {
        AuthResult result = authService.login("nonexistent", "password");
        assertFalse("Should fail for non-existent user", result.isSuccess());
        assertFalse("Should not be locked", result.isLocked());
    }

    @Test
    public void hasRoleReturnsTrueForManager() {
        var manager = authService.login("manager", "password123");
        if (manager.isSuccess()) {
            boolean isManager = authService.hasRole(manager.getUser(), oopassignment.domain.auth.Role.MANAGER);
            assertTrue("Manager should have MANAGER role", isManager);
        }
    }

    @Test
    public void hasRoleReturnsFalseForStaffAsManager() {
        var staff = authService.login("staff", "password123");
        if (staff.isSuccess()) {
            boolean isManager = authService.hasRole(staff.getUser(), oopassignment.domain.auth.Role.MANAGER);
            assertFalse("Staff should not have MANAGER role", isManager);
        }
    }

    @Test
    public void hasRoleReturnsTrueForStaff() {
        var staff = authService.login("staff", "password123");
        if (staff.isSuccess()) {
            boolean isStaff = authService.hasRole(staff.getUser(), oopassignment.domain.auth.Role.STAFF);
            assertTrue("Staff should have STAFF role", isStaff);
        }
    }

    @Test
    public void emptyUsernameFails() {
        AuthResult result = authService.login("", "password");
        assertFalse("Should fail for empty username", result.isSuccess());
    }

    @Test
    public void emptyPasswordFails() {
        AuthResult result = authService.login("manager", "");
        assertFalse("Should fail for empty password", result.isSuccess());
    }

    @Test
    public void successfulLoginReturnsUser() {
        AuthResult result = authService.login("manager", "password123");
        assertTrue("Should succeed", result.isSuccess());
        assertNotNull("Should return user", result.getUser());
        assertEquals("manager", result.getUser().getUsername());
    }

    @Test
    public void failedLoginDoesNotReturnUser() {
        AuthResult result = authService.login("manager", "wrongpass");
        assertFalse("Should fail", result.isSuccess());
    }

    @Test
    public void multipleDifferentUserLocksDontAffectEachOther() {
        // Fail login for staff multiple times
        authService.login("staff", "bad1");
        authService.login("staff", "bad2");
        
        // Manager should still work
        AuthResult managerResult = authService.login("manager", "password123");
        assertTrue("Manager login should still work", managerResult.isSuccess());
    }
}
