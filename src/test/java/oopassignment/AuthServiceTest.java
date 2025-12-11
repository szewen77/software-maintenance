package oopassignment;

import oopassignment.repository.EmployeeRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.service.AuthService;
import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
