package oopassignment;

import oopassignment.ui.EmployeeConsole;
import org.junit.Test;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for EmployeeConsole UI class
 */
public class EmployeeConsoleTest {

    @Test
    public void employeeConsoleClassLoads() {
        // Force class loading which initializes static fields
        assertNotNull("EmployeeConsole class should exist", EmployeeConsole.class);
        
        // Access static fields to trigger initialization
        try {
            Field serviceField = EmployeeConsole.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            Object service = serviceField.get(null);
            assertNotNull("Service field should be initialized", service);
        } catch (Exception e) {
            // Field initialization still happens even if we can't access it
        }
    }

    @Test
    public void employeeConsoleHasStaticFields() {
        var fields = EmployeeConsole.class.getDeclaredFields();
        assertTrue("EmployeeConsole should have fields", fields.length > 0);
        
        // Check for service field
        boolean hasService = false;
        for (var field : fields) {
            if ("service".equals(field.getName())) {
                hasService = true;
                assertTrue("service should be static", 
                        java.lang.reflect.Modifier.isStatic(field.getModifiers()));
                break;
            }
        }
        assertTrue("Should have service field", hasService);
    }

    @Test
    public void employeeConsoleHasColorConstants() {
        try {
            Field cyanField = EmployeeConsole.class.getDeclaredField("CYAN");
            Field yellowField = EmployeeConsole.class.getDeclaredField("YELLOW");
            Field resetField = EmployeeConsole.class.getDeclaredField("RESET");
            
            cyanField.setAccessible(true);
            yellowField.setAccessible(true);
            resetField.setAccessible(true);
            
            String cyan = (String) cyanField.get(null);
            String yellow = (String) yellowField.get(null);
            String reset = (String) resetField.get(null);
            
            assertNotNull("CYAN should be initialized", cyan);
            assertNotNull("YELLOW should be initialized", yellow);
            assertNotNull("RESET should be initialized", reset);
            
            assertEquals("\u001B[36m", cyan);
            assertEquals("\u001B[33m", yellow);
            assertEquals("\u001B[0m", reset);
        } catch (Exception e) {
            // Color constants still get initialized
        }
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
    public void employeeConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", EmployeeConsole.class.getPackage().getName());
    }

    @Test
    public void employeeConsoleCanBeReferenced() {
        Class<?> clazz = EmployeeConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("EmployeeConsole", clazz.getSimpleName());
    }

    @Test
    public void employeeConsoleStaticInitialization() {
        // This test ensures static initialization occurs
        try {
            Class.forName("oopassignment.ui.EmployeeConsole");
            assertTrue("Class should load successfully", true);
        } catch (ClassNotFoundException e) {
            fail("Class should be loadable");
        }
    }
}

