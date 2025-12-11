/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oopassignment.ui;

/**
 *
 * @author USER
 */
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class BootsDo {

    public static Scanner input = new Scanner(System.in);
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BLACK = "\u001B[0m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws IOException {
        BootsDotDo_Logo();
        startApplication();
    }
    
    public static void startApplication() throws IOException {
        while (true) {
            System.out.println("\n" + ANSI_CYAN + "=== BOOTS.DO SYSTEM ===" + RESET);
            System.out.println("1. Login");
            System.out.println("2. Exit System");
            int choice = readIntInRange("\nOption > ", 1, 2);
            
            if (choice == 1) {
                clearJavaConsoleScreen();
                BootsDotDo_Logo();
                // login() returns true if user logged out or chose not to retry
                // This will bring them back to the initial menu
                LoginConsole.login();
                clearJavaConsoleScreen();
                BootsDotDo_Logo();
            } else if (choice == 2) {
                System.out.println(ANSI_GREEN + "\nThank you for using Boots.Do. Goodbye!" + RESET);
                System.exit(0);
            }
        }
    }

    public static void BootsDotDo_Logo(){
        String purple = "\u001B[35m"; // 
        String resetColor = "\u001B[0m"; // Reset color to default
        System.out.println(purple + ".-. .-')                             .-') _     .-')      _ .-') _                                               .-') _               ('-.   .-') _    ");
        System.out.println(purple + "\\  ( OO )                           (  OO) )   ( OO ).   ( (  OO) )                                             (  OO) )            _(  OO) (  OO) )  ");
        System.out.println(purple + " ;-----.\\  .-'),-----.  .-'),-----. /     '._ (_)---\\_)   \\     .'_  .-'),-----.        .-'),-----.  ,--. ,--.  /     '._ ,--.     (,------./     '._ ");
        System.out.println(purple + " | .-.  | ( OO'  .-.  '( OO'  .-.  '|'--...__)/    _ |    ,`'--..._)( OO'  .-.  '      ( OO'  .-.  ' |  | |  |  |'--...__)|  |.-')  |  .---'|'--...__)");
        System.out.println(purple + " | '-' /_)/   |  | |  |/   |  | |  |'--.  .--'\\  :` `.    |  |  \\  '/   |  | |  |      /   |  | |  | |  | | .-')'--.  .--'|  | OO ) |  |    '--.  .--' ");
        System.out.println(purple + " | .-. `. \\_) |  |\\|  |\\_) |  |\\|  |   |  |    '..`''.)   |  |   ' |\\_) |  |\\|  |      \\_) |  |\\|  | |  |_|( OO )  |  |   |  |`-' |(|  '--.    |  |    ");
        System.out.println(purple + " | |  \\  |  \\ |  | |  |  \\ |  | |  |   |  |   .-._)   \\   |  |   / :  \\ |  | |  |        \\ |  | |  | |  | | `-' /  |  |  (|  '---.' |  .--'    |  |   ");
        System.out.println(purple + " | '--'  /   `'  '-'  '   `'  '-'  '   |  |   \\       /.-.|  '--'  /   `'  '-'  '         `'  '-'  '('  '-'(_.-'   |  |   |      |  |  `---.   |  |    ");
        System.out.println(purple + " `------'      `-----'      `-----'    `--'    `-----' `-'`-------'      `-----'            `-----'   `-----'      `--'   `------'  `------'   `--'   ");
        System.out.println(resetColor);
        System.out.println("\nWelcome to Boots.Do");
    }
    
    public static void clearJavaConsoleScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    

    /**
     * Read an integer within the specified range, re-prompting on bad input.
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = input.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.println(ANSI_RED + "Please enter a number between " + min + " and " + max + "." + ANSI_BLACK);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println(ANSI_RED + "Invalid input, please enter a valid integer." + ANSI_BLACK);
            }
        }
    }

    public static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = input.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value <= 0) {
                    System.out.println(ANSI_RED + "Value must be greater than 0." + ANSI_BLACK);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println(ANSI_RED + "Invalid number, please try again." + ANSI_BLACK);
            }
        }
    }

    public static double readDouble(String prompt, double minValue) {
        while (true) {
            System.out.print(prompt);
            String raw = input.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (Double.isNaN(value) || value < minValue) {
                    System.out.println(ANSI_RED + "Please enter a value of at least " + minValue + "." + ANSI_BLACK);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println(ANSI_RED + "Invalid number, please try again." + ANSI_BLACK);
            }
        }
    }

    public static Double readOptionalDouble(String prompt, double minValue) {
        while (true) {
            System.out.print(prompt);
            String raw = input.nextLine().trim();
            if (raw.isEmpty()) {
                return null;
            }
            try {
                double value = Double.parseDouble(raw);
                if (Double.isNaN(value) || value < minValue) {
                    System.out.println(ANSI_RED + "Please enter a value of at least " + minValue + "." + ANSI_BLACK);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println(ANSI_RED + "Invalid number, please try again." + ANSI_BLACK);
            }
        }
    }

    public static String readRequiredLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println(ANSI_RED + "This field cannot be blank." + ANSI_BLACK);
        }
    }

    public static String readOptionalLine(String prompt) {
        System.out.print(prompt);
        String line = input.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim().toUpperCase();
            if ("Y".equals(value)) {
                return true;
            }
            if ("N".equals(value)) {
                return false;
            }
            System.out.println(ANSI_RED + "Please enter Y or N." + ANSI_BLACK);
        }
    }
}
