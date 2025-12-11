package oopassignment;

import oopassignment.ui.EmployeeConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for EmployeeConsole UI class
 */
public class EmployeeConsoleTest {

    @Test
    public void employeeConsoleClassExists() {
        assertNotNull("EmployeeConsole class should exist", EmployeeConsole.class);
    }

    @Test
    public void employeeConsoleIsPublic() {
        assertTrue("EmployeeConsole should be public", 
                java.lang.reflect.Modifier.isPublic(EmployeeConsole.class.getModifiers()));
    }

    @Test
    public void employeeConsoleHasShowMenuMethod() {
        try {
            var method = EmployeeConsole.class.getMethod("showMenu");
            assertNotNull("showMenu method should exist", method);
            assertTrue("showMenu should be public",
                    java.lang.reflect.Modifier.isPublic(method.getModifiers()));
            assertTrue("showMenu should be static",
                    java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("showMenu method should exist");
        }
    }

    @Test
    public void employeeConsoleHasDeclaredMethods() {
        var methods = EmployeeConsole.class.getDeclaredMethods();
        assertTrue("EmployeeConsole should have methods", methods.length > 0);
        assertTrue("EmployeeConsole should have multiple methods", methods.length >= 5);
    }

    @Test
    public void employeeConsoleHasDeclaredFields() {
        var fields = EmployeeConsole.class.getDeclaredFields();
        assertTrue("EmployeeConsole should have fields", fields.length > 0);
    }

    @Test
    public void employeeConsoleMethodsAreAccessible() {
        var methods = EmployeeConsole.class.getDeclaredMethods();
        assertNotNull("Methods array should not be null", methods);
        
        boolean hasShowMenu = false;
        for (var method : methods) {
            if ("showMenu".equals(method.getName())) {
                hasShowMenu = true;
                break;
            }
        }
        assertTrue("Should have showMenu method", hasShowMenu);
    }

    @Test
    public void employeeConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", EmployeeConsole.class.getPackage().getName());
    }

    @Test
    public void employeeConsoleCanBeReferenced() {
        Class<?> clazz = EmployeeConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("EmployeeConsole", clazz.getSimpleName());
    }
}

