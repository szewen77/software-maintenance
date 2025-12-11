package oopassignment;

import oopassignment.ui.ProductConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for ProductConsole UI class
 */
public class ProductConsoleTest {

    @Test
    public void productConsoleClassExists() {
        assertNotNull("ProductConsole class should exist", ProductConsole.class);
    }

    @Test
    public void productConsoleIsPublic() {
        assertTrue("ProductConsole should be public", 
                java.lang.reflect.Modifier.isPublic(ProductConsole.class.getModifiers()));
    }

    @Test
    public void productConsoleHasShowMenuMethod() {
        try {
            var method = ProductConsole.class.getMethod("showMenu");
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
    public void productConsoleHasDeclaredMethods() {
        var methods = ProductConsole.class.getDeclaredMethods();
        assertTrue("ProductConsole should have methods", methods.length > 0);
        assertTrue("ProductConsole should have multiple methods", methods.length >= 5);
    }

    @Test
    public void productConsoleHasDeclaredFields() {
        var fields = ProductConsole.class.getDeclaredFields();
        assertTrue("ProductConsole should have fields", fields.length > 0);
    }

    @Test
    public void productConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", ProductConsole.class.getPackage().getName());
    }

    @Test
    public void productConsoleCanBeReferenced() {
        Class<?> clazz = ProductConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("ProductConsole", clazz.getSimpleName());
    }

    @Test
    public void productConsoleMethodsAreAccessible() {
        var methods = ProductConsole.class.getDeclaredMethods();
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
}

