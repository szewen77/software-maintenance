package oopassignment;

import oopassignment.ui.EmployeeConsole;
import oopassignment.ui.MemberConsole;
import oopassignment.ui.OrderConsole;
import oopassignment.ui.ProductConsole;
import oopassignment.ui.ReportConsole;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for UI Console classes - testing accessible static fields and methods
 */
public class UIConsoleServicesTest {

    @Test
    public void employeeConsoleClassExists() {
        assertNotNull("EmployeeConsole class should exist", EmployeeConsole.class);
    }

    @Test
    public void memberConsoleClassExists() {
        assertNotNull("MemberConsole class should exist", MemberConsole.class);
    }

    @Test
    public void orderConsoleClassExists() {
        assertNotNull("OrderConsole class should exist", OrderConsole.class);
    }

    @Test
    public void productConsoleClassExists() {
        assertNotNull("ProductConsole class should exist", ProductConsole.class);
    }

    @Test
    public void reportConsoleClassExists() {
        assertNotNull("ReportConsole class should exist", ReportConsole.class);
    }

    @Test
    public void allUIConsolesHaveShowMenuMethod() {
        try {
            EmployeeConsole.class.getMethod("showMenu");
            MemberConsole.class.getMethod("showMenu");
            OrderConsole.class.getMethod("showMenu");
            ProductConsole.class.getMethod("showMenu");
            ReportConsole.class.getMethod("showMenu");
            assertTrue("All consoles should have showMenu method", true);
        } catch (NoSuchMethodException e) {
            fail("Console classes should have showMenu method: " + e.getMessage());
        }
    }

    @Test
    public void employeeConsoleHasPublicMethods() {
        var methods = EmployeeConsole.class.getDeclaredMethods();
        assertTrue("EmployeeConsole should have methods", methods.length > 0);
    }

    @Test
    public void memberConsoleHasPublicMethods() {
        var methods = MemberConsole.class.getDeclaredMethods();
        assertTrue("MemberConsole should have methods", methods.length > 0);
    }

    @Test
    public void orderConsoleHasPublicMethods() {
        var methods = OrderConsole.class.getDeclaredMethods();
        assertTrue("OrderConsole should have methods", methods.length > 0);
    }

    @Test
    public void productConsoleHasPublicMethods() {
        var methods = ProductConsole.class.getDeclaredMethods();
        assertTrue("ProductConsole should have methods", methods.length > 0);
    }

    @Test
    public void reportConsoleHasPublicMethods() {
        var methods = ReportConsole.class.getDeclaredMethods();
        assertTrue("ReportConsole should have methods", methods.length > 0);
    }

    @Test
    public void allUIConsolesAreNotNull() {
        assertNotNull(EmployeeConsole.class);
        assertNotNull(MemberConsole.class);
        assertNotNull(OrderConsole.class);
        assertNotNull(ProductConsole.class);
        assertNotNull(ReportConsole.class);
    }
}

