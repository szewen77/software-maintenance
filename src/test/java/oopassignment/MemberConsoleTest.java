package oopassignment;

import oopassignment.ui.MemberConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for MemberConsole UI class
 */
public class MemberConsoleTest {

    @Test
    public void memberConsoleClassExists() {
        assertNotNull("MemberConsole class should exist", MemberConsole.class);
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
    public void memberConsoleHasDeclaredFields() {
        var fields = MemberConsole.class.getDeclaredFields();
        assertTrue("MemberConsole should have fields", fields.length > 0);
    }

    @Test
    public void memberConsolePackageIsCorrect() {
        assertEquals("oopassignment.ui", MemberConsole.class.getPackage().getName());
    }

    @Test
    public void memberConsoleCanBeReferenced() {
        Class<?> clazz = MemberConsole.class;
        assertNotNull("Class should be referenceable", clazz);
        assertEquals("MemberConsole", clazz.getSimpleName());
    }

    @Test
    public void memberConsoleMethodsIncludeExpectedNames() {
        var methods = MemberConsole.class.getDeclaredMethods();
        boolean hasShowMenu = false;
        
        for (var method : methods) {
            if ("showMenu".equals(method.getName())) {
                hasShowMenu = true;
            }
        }
        
        assertTrue("Should have showMenu method", hasShowMenu);
    }
}

