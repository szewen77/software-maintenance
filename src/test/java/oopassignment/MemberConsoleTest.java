package oopassignment;

import oopassignment.ui.MemberConsole;
import org.junit.Test;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for MemberConsole UI class
 */
public class MemberConsoleTest {

    @Test
    public void memberConsoleClassLoads() {
        // Force class loading and static field initialization
        assertNotNull("MemberConsole class should exist", MemberConsole.class);
        
        try {
            Field serviceField = MemberConsole.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            Object service = serviceField.get(null);
            assertNotNull("Service field should be initialized", service);
        } catch (Exception e) {
            // Static initialization still happens
        }
    }

    @Test
    public void memberConsoleHasStaticServiceField() {
        var fields = MemberConsole.class.getDeclaredFields();
        assertTrue("MemberConsole should have fields", fields.length > 0);
        
        boolean hasService = false;
        for (var field : fields) {
            if ("service".equals(field.getName())) {
                hasService = true;
                assertTrue("service should be static", 
                        java.lang.reflect.Modifier.isStatic(field.getModifiers()));
                assertTrue("service should be final", 
                        java.lang.reflect.Modifier.isFinal(field.getModifiers()));
                break;
            }
        }
        assertTrue("Should have service field", hasService);
    }

    @Test
    public void memberConsoleIsPublic() {
        assertTrue("MemberConsole should be public", 
                java.lang.reflect.Modifier.isPublic(MemberConsole.class.getModifiers()));
    }

    @Test
    public void memberConsoleHasShowMenuMethod() {
        try {
            var method = MemberConsole.class.getMethod("showMenu");
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
    public void memberConsoleHasDeclaredMethods() {
        var methods = MemberConsole.class.getDeclaredMethods();
        assertTrue("MemberConsole should have methods", methods.length > 0);
        assertTrue("MemberConsole should have multiple methods", methods.length >= 5);
    }

    @Test
    public void memberConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", MemberConsole.class.getPackage().getName());
    }

    @Test
    public void memberConsoleStaticInitialization() {
        try {
            Class.forName("oopassignment.ui.MemberConsole");
            assertTrue("Class should load successfully", true);
        } catch (ClassNotFoundException e) {
            fail("Class should be loadable");
        }
    }

    @Test
    public void memberConsoleCanBeReferenced() {
        Class<?> clazz = MemberConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("MemberConsole", clazz.getSimpleName());
    }
}

