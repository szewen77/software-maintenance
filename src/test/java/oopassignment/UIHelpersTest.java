package oopassignment;

import oopassignment.ui.BootsDo;
import oopassignment.ui.LoginConsole;
import org.junit.Test;

import static org.junit.Assert.*;

public class UIHelpersTest {

    @Test
    public void bootsDoConstantsAreDefined() {
        assertNotNull("ANSI_RED should be defined", BootsDo.ANSI_RED);
        assertNotNull("ANSI_GREEN should be defined", BootsDo.ANSI_GREEN);
        assertNotNull("ANSI_YELLOW should be defined", BootsDo.ANSI_YELLOW);
        assertNotNull("ANSI_CYAN should be defined", BootsDo.ANSI_CYAN);
        assertNotNull("ANSI_BLACK should be defined", BootsDo.ANSI_BLACK);
        assertNotNull("RESET should be defined", BootsDo.RESET);
    }

    @Test
    public void ansiCodesAreNotEmpty() {
        assertFalse("ANSI_RED should not be empty", BootsDo.ANSI_RED.isEmpty());
        assertFalse("ANSI_GREEN should not be empty", BootsDo.ANSI_GREEN.isEmpty());
        assertFalse("ANSI_YELLOW should not be empty", BootsDo.ANSI_YELLOW.isEmpty());
        assertFalse("ANSI_CYAN should not be empty", BootsDo.ANSI_CYAN.isEmpty());
        assertFalse("ANSI_BLACK should not be empty", BootsDo.ANSI_BLACK.isEmpty());
        assertFalse("RESET should not be empty", BootsDo.RESET.isEmpty());
    }

    @Test
    public void ansiCodesAreEscapeSequences() {
        assertTrue("ANSI_RED should be escape sequence", 
                BootsDo.ANSI_RED.startsWith("\u001B"));
        assertTrue("ANSI_GREEN should be escape sequence", 
                BootsDo.ANSI_GREEN.startsWith("\u001B"));
        assertTrue("ANSI_YELLOW should be escape sequence", 
                BootsDo.ANSI_YELLOW.startsWith("\u001B"));
        assertTrue("ANSI_CYAN should be escape sequence", 
                BootsDo.ANSI_CYAN.startsWith("\u001B"));
        assertTrue("ANSI_BLACK should be escape sequence", 
                BootsDo.ANSI_BLACK.startsWith("\u001B"));
        assertTrue("RESET should be escape sequence", 
                BootsDo.RESET.startsWith("\u001B"));
    }

    @Test
    public void loginConsoleGetCashierAccessible() {
        // getCashier should be accessible (may be null before login)
        try {
            LoginConsole.getCashier();
            assertTrue("getCashier method should be accessible", true);
        } catch (Exception e) {
            fail("getCashier should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void loginConsoleGetCurrentUserAccessible() {
        // getCurrentUser should be accessible (may be null before login)
        try {
            LoginConsole.getCurrentUser();
            assertTrue("getCurrentUser method should be accessible", true);
        } catch (Exception e) {
            fail("getCurrentUser should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void bootsDoInputScannerExists() {
        assertNotNull("Scanner input should exist", BootsDo.input);
    }

    @Test
    public void bootsDoHasColorConstants() {
        // Verify all color constants exist and are different
        assertNotEquals("RED and GREEN should be different", 
                BootsDo.ANSI_RED, BootsDo.ANSI_GREEN);
        assertNotEquals("RED and YELLOW should be different", 
                BootsDo.ANSI_RED, BootsDo.ANSI_YELLOW);
        assertNotEquals("GREEN and YELLOW should be different", 
                BootsDo.ANSI_GREEN, BootsDo.ANSI_YELLOW);
    }

    @Test
    public void resetColorIsDifferentFromOthers() {
        assertNotEquals("RESET should be different from RED", 
                BootsDo.RESET, BootsDo.ANSI_RED);
        assertNotEquals("RESET should be different from GREEN", 
                BootsDo.RESET, BootsDo.ANSI_GREEN);
        assertNotEquals("RESET should be different from YELLOW", 
                BootsDo.RESET, BootsDo.ANSI_YELLOW);
    }

    @Test
    public void ansiBlackEqualsReset() {
        // ANSI_BLACK and RESET should be the same (both reset to default)
        assertEquals("ANSI_BLACK should equal RESET", 
                BootsDo.ANSI_BLACK, BootsDo.RESET);
    }

    @Test
    public void colorCodesAreProperFormat() {
        // ANSI codes should follow the pattern \u001B[XXm
        assertTrue("ANSI_RED should contain [", BootsDo.ANSI_RED.contains("["));
        assertTrue("ANSI_GREEN should contain [", BootsDo.ANSI_GREEN.contains("["));
        assertTrue("ANSI_YELLOW should contain [", BootsDo.ANSI_YELLOW.contains("["));
        assertTrue("ANSI_CYAN should contain [", BootsDo.ANSI_CYAN.contains("["));
    }

    @Test
    public void allColorsHaveExpectedLength() {
        // ANSI color codes typically have specific lengths
        assertTrue("ANSI_RED should have reasonable length", 
                BootsDo.ANSI_RED.length() > 3 && BootsDo.ANSI_RED.length() < 10);
        assertTrue("ANSI_GREEN should have reasonable length", 
                BootsDo.ANSI_GREEN.length() > 3 && BootsDo.ANSI_GREEN.length() < 10);
        assertTrue("ANSI_YELLOW should have reasonable length", 
                BootsDo.ANSI_YELLOW.length() > 3 && BootsDo.ANSI_YELLOW.length() < 10);
    }
}

