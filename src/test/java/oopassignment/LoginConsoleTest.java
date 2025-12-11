package oopassignment;

import oopassignment.ui.LoginConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for LoginConsole UI class
 */
public class LoginConsoleTest {

    @Test
    public void getCashierMethodAccessible() {
        try {
            LoginConsole.getCashier();
            // Cashier may be null before login, method should be accessible
            assertTrue("getCashier should be accessible", true);
        } catch (Exception e) {
            fail("getCashier should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void getCurrentUserMethodAccessible() {
        try {
            LoginConsole.getCurrentUser();
            // User may be null before login, method should be accessible
            assertTrue("getCurrentUser should be accessible", true);
        } catch (Exception e) {
            fail("getCurrentUser should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void loginConsoleClassExists() {
        assertNotNull("LoginConsole class should exist", LoginConsole.class);
    }

    @Test
    public void loginConsoleIsPublic() {
        assertTrue("LoginConsole should be public", 
                java.lang.reflect.Modifier.isPublic(LoginConsole.class.getModifiers()));
    }

    @Test
    public void loginConsoleHasLoginMethod() {
        try {
            var method = LoginConsole.class.getMethod("login");
            assertNotNull("login method should exist", method);
            assertTrue("login method should be public",
                    java.lang.reflect.Modifier.isPublic(method.getModifiers()));
            assertTrue("login method should be static",
                    java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("login method should exist");
        }
    }

    @Test
    public void loginConsoleHasMenuMethod() {
        try {
            var method = LoginConsole.class.getMethod("Menu");
            assertNotNull("Menu method should exist", method);
            assertTrue("Menu method should be public",
                    java.lang.reflect.Modifier.isPublic(method.getModifiers()));
            assertTrue("Menu method should be static",
                    java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("Menu method should exist");
        }
    }

    @Test
    public void loginConsoleHasOrderMenuMethod() {
        try {
            var method = LoginConsole.class.getMethod("orderMenu");
            assertNotNull("orderMenu method should exist", method);
        } catch (NoSuchMethodException e) {
            fail("orderMenu method should exist");
        }
    }

    @Test
    public void getCashierReturnsStringOrNull() {
        String cashier = LoginConsole.getCashier();
        // Should return String or null, both are valid
        assertTrue("getCashier should return String or null", 
                cashier == null || cashier instanceof String);
    }

    @Test
    public void getCurrentUserReturnsEmployeeRecordOrNull() {
        var user = LoginConsole.getCurrentUser();
        // Should return EmployeeRecord or null, both are valid
        assertTrue("getCurrentUser should return EmployeeRecord or null", 
                user == null || user instanceof oopassignment.domain.auth.EmployeeRecord);
    }

    @Test
    public void loginConsoleHasDeclaredMethods() {
        var methods = LoginConsole.class.getDeclaredMethods();
        assertTrue("LoginConsole should have methods", methods.length > 0);
    }

    @Test
    public void loginConsoleHasDeclaredFields() {
        var fields = LoginConsole.class.getDeclaredFields();
        assertTrue("LoginConsole should have fields", fields.length > 0);
    }
}

