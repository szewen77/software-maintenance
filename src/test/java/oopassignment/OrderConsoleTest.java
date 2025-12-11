package oopassignment;

import oopassignment.ui.OrderConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for OrderConsole UI class
 */
public class OrderConsoleTest {

    @Test
    public void orderConsoleClassExists() {
        assertNotNull("OrderConsole class should exist", OrderConsole.class);
    }

    @Test
    public void orderConsoleIsPublic() {
        assertTrue("OrderConsole should be public", 
                java.lang.reflect.Modifier.isPublic(OrderConsole.class.getModifiers()));
    }

    @Test
    public void orderConsoleHasShowMenuMethod() {
        try {
            var method = OrderConsole.class.getMethod("showMenu");
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
    public void orderConsoleHasStartOrderFlowMethod() {
        try {
            var method = OrderConsole.class.getMethod("startOrderFlow");
            assertNotNull("startOrderFlow method should exist", method);
        } catch (NoSuchMethodException e) {
            fail("startOrderFlow method should exist");
        }
    }

    @Test
    public void orderConsoleHasDeclaredMethods() {
        var methods = OrderConsole.class.getDeclaredMethods();
        assertTrue("OrderConsole should have methods", methods.length > 0);
    }

    @Test
    public void orderConsoleHasDeclaredFields() {
        var fields = OrderConsole.class.getDeclaredFields();
        assertTrue("OrderConsole should have fields", fields.length > 0);
    }

    @Test
    public void orderConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", OrderConsole.class.getPackage().getName());
    }

    @Test
    public void orderConsoleCanBeReferenced() {
        Class<?> clazz = OrderConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("OrderConsole", clazz.getSimpleName());
    }

    @Test
    public void orderConsoleHasExpectedMethods() {
        var methods = OrderConsole.class.getDeclaredMethods();
        boolean hasShowMenu = false;
        boolean hasStartOrderFlow = false;
        
        for (var method : methods) {
            if ("showMenu".equals(method.getName())) {
                hasShowMenu = true;
            }
            if ("startOrderFlow".equals(method.getName())) {
                hasStartOrderFlow = true;
            }
        }
        
        assertTrue("Should have showMenu method", hasShowMenu);
        assertTrue("Should have startOrderFlow method", hasStartOrderFlow);
    }
}

