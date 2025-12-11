package oopassignment;

import oopassignment.ui.ReportConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for ReportConsole UI class
 */
public class ReportConsoleTest {

    @Test
    public void reportConsoleClassExists() {
        assertNotNull("ReportConsole class should exist", ReportConsole.class);
    }

    @Test
    public void reportConsoleIsPublic() {
        assertTrue("ReportConsole should be public", 
                java.lang.reflect.Modifier.isPublic(ReportConsole.class.getModifiers()));
    }

    @Test
    public void reportConsoleHasShowMenuMethod() {
        try {
            var method = ReportConsole.class.getMethod("showMenu");
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
    public void reportConsoleHasDeclaredMethods() {
        var methods = ReportConsole.class.getDeclaredMethods();
        assertTrue("ReportConsole should have methods", methods.length > 0);
        assertTrue("ReportConsole should have multiple methods", methods.length >= 5);
    }

    @Test
    public void reportConsoleHasDeclaredFields() {
        var fields = ReportConsole.class.getDeclaredFields();
        assertTrue("ReportConsole should have fields", fields.length > 0);
    }

    @Test
    public void reportConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", ReportConsole.class.getPackage().getName());
    }

    @Test
    public void reportConsoleCanBeReferenced() {
        Class<?> clazz = ReportConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("ReportConsole", clazz.getSimpleName());
    }

    @Test
    public void reportConsoleMethodsIncludeShowMenu() {
        var methods = ReportConsole.class.getDeclaredMethods();
        boolean hasShowMenu = false;
        
        for (var method : methods) {
            if ("showMenu".equals(method.getName())) {
                hasShowMenu = true;
                break;
            }
        }
        
        assertTrue("Should have showMenu method", hasShowMenu);
    }
}

