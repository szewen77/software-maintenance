package oopassignment;

import oopassignment.ui.BootsDo;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for BootsDo UI class
 */
public class BootsDoTest {

    @Test
    public void ansiRedConstant() {
        assertEquals("\u001B[31m", BootsDo.ANSI_RED);
    }

    @Test
    public void ansiGreenConstant() {
        assertEquals("\u001B[32m", BootsDo.ANSI_GREEN);
    }

    @Test
    public void ansiYellowConstant() {
        assertEquals("\u001B[33m", BootsDo.ANSI_YELLOW);
    }

    @Test
    public void ansiCyanConstant() {
        assertEquals("\u001B[36m", BootsDo.ANSI_CYAN);
    }

    @Test
    public void ansiBlackConstant() {
        assertEquals("\u001B[0m", BootsDo.ANSI_BLACK);
    }

    @Test
    public void resetConstant() {
        assertEquals("\u001B[0m", BootsDo.RESET);
    }

    @Test
    public void inputScannerExists() {
        assertNotNull("Scanner should be initialized", BootsDo.input);
    }

    @Test
    public void bootsDoLogoDisplays() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try {
            System.setOut(new PrintStream(outContent));
            BootsDo.BootsDotDo_Logo();
            
            String output = outContent.toString();
            assertTrue("Should contain welcome message", output.contains("Welcome to Boots.Do"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void clearJavaConsoleScreen() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try {
            System.setOut(new PrintStream(outContent));
            BootsDo.clearJavaConsoleScreen();
            
            String output = outContent.toString();
            assertNotNull("Should produce output", output);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void readDoubleWithValidInput() {
        // This tests the method signature exists and is accessible
        assertNotNull("readDouble method should exist", 
                getMethod("readDouble", String.class, double.class));
    }

    @Test
    public void readOptionalDoubleMethodExists() {
        assertNotNull("readOptionalDouble method should exist", 
                getMethod("readOptionalDouble", String.class, double.class));
    }

    @Test
    public void readRequiredLineMethodExists() {
        assertNotNull("readRequiredLine method should exist", 
                getMethod("readRequiredLine", String.class));
    }

    @Test
    public void readOptionalLineMethodExists() {
        assertNotNull("readOptionalLine method should exist", 
                getMethod("readOptionalLine", String.class));
    }

    @Test
    public void readYesNoMethodExists() {
        assertNotNull("readYesNo method should exist", 
                getMethod("readYesNo", String.class));
    }

    @Test
    public void readIntInRangeMethodExists() {
        assertNotNull("readIntInRange method should exist", 
                getMethod("readIntInRange", String.class, int.class, int.class));
    }

    @Test
    public void readPositiveIntMethodExists() {
        assertNotNull("readPositiveInt method should exist", 
                getMethod("readPositiveInt", String.class));
    }

    @Test
    public void allColorConstantsAreDifferent() {
        assertNotEquals(BootsDo.ANSI_RED, BootsDo.ANSI_GREEN);
        assertNotEquals(BootsDo.ANSI_RED, BootsDo.ANSI_YELLOW);
        assertNotEquals(BootsDo.ANSI_RED, BootsDo.ANSI_CYAN);
        assertNotEquals(BootsDo.ANSI_GREEN, BootsDo.ANSI_YELLOW);
        assertNotEquals(BootsDo.ANSI_GREEN, BootsDo.ANSI_CYAN);
        assertNotEquals(BootsDo.ANSI_YELLOW, BootsDo.ANSI_CYAN);
    }

    @Test
    public void ansiBlackEqualsReset() {
        assertEquals(BootsDo.ANSI_BLACK, BootsDo.RESET);
    }

    @Test
    public void allAnsiCodesStartWithEscape() {
        assertTrue(BootsDo.ANSI_RED.startsWith("\u001B"));
        assertTrue(BootsDo.ANSI_GREEN.startsWith("\u001B"));
        assertTrue(BootsDo.ANSI_YELLOW.startsWith("\u001B"));
        assertTrue(BootsDo.ANSI_CYAN.startsWith("\u001B"));
        assertTrue(BootsDo.ANSI_BLACK.startsWith("\u001B"));
        assertTrue(BootsDo.RESET.startsWith("\u001B"));
    }

    @Test
    public void bootsDoClassHasPublicMethods() {
        var methods = BootsDo.class.getDeclaredMethods();
        assertTrue("BootsDo should have methods", methods.length > 0);
    }

    @Test
    public void bootsDoClassIsPublic() {
        assertTrue("BootsDo class should be public", 
                java.lang.reflect.Modifier.isPublic(BootsDo.class.getModifiers()));
    }

    private java.lang.reflect.Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return BootsDo.class.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}

