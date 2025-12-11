/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oopassignment.ui;

/**
 *
 * @author USER
 */
import java.io.IOException;

import oopassignment.AuthResult;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.ANSI_BLACK;
import static oopassignment.ui.BootsDo.ANSI_RED;
import static oopassignment.ui.BootsDo.clearJavaConsoleScreen;
import static oopassignment.ui.BootsDo.readIntInRange;
import static oopassignment.ui.BootsDo.readRequiredLine;
import static oopassignment.ui.BootsDo.readYesNo;

public class LoginConsole {

    static int check;
    private static String Cashier;
    private static EmployeeRecord currentUser;
    
    public static String getCashier(){
        return Cashier;
    }

    public static EmployeeRecord getCurrentUser() {
        return currentUser;
    }
    

    public static void login() {
        while (true) {
            System.out.println("\nLOGIN");
            String username = readRequiredLine("Username : ");
            String password = readRequiredLine("Password : ");

            AuthResult result = ApplicationContext.AUTH_SERVICE.login(username, password);
            if (result.isSuccess()) {
                currentUser = result.getUser();
                Cashier = currentUser.getUsername();
                check = ApplicationContext.AUTH_SERVICE.hasRole(currentUser, Role.MANAGER) ? 1 : 2;
                clearJavaConsoleScreen();
                System.out.printf("\nHi, %s \n", Cashier);
                boolean logout = Menu();
                if (logout) {
                    currentUser = null;
                    Cashier = null;
                    clearJavaConsoleScreen();
                    continue;
                }
                return;
            }

            System.out.println(ANSI_RED + result.getMessage() + ANSI_BLACK);
            if (result.isLocked()) {
                continue;
            }
            if (!promptRetry()) {
                break;
            }
        }
    }

    // Legacy compatibility: delegate old orderMenu calls to the new order flow.
    public static void orderMenu() throws IOException {
        OrderConsole.startOrderFlow();
    }

    private static boolean promptRetry() {
        return readYesNo("Try again (Y/N)? ");
    }

    public static boolean Menu() {
        if (LoginConsole.check == 1) {
            int opt = 0;
            // manager menu (no order here)
            do {
                System.out.println("""
                               
                               MAIN MENU
                               1. Employee Management
                               2. Product & Stock Management
                               3. Member Management
                               4. Reports
                               5. Log out
                               """);
                opt = readIntInRange("Option > ", 1, 5);

                switch (opt) {
                    case 1 -> {
                        clearJavaConsoleScreen();
                        EmployeeConsole.showMenu();
                        clearJavaConsoleScreen();
                    }
                    case 2 -> ProductConsole.showMenu();
                    case 3 -> {
                        clearJavaConsoleScreen();
                        MemberConsole.showMenu();
                        clearJavaConsoleScreen();
                    }
                    case 4 -> {
                        clearJavaConsoleScreen();
                        ReportConsole.showMenu();
                        clearJavaConsoleScreen();
                    }
                    case 5 -> {
                        logout();
                        return true;
                    }
                    default -> System.out.println(ANSI_RED + "Invalid input, please enter a number between 1 and 5 !!!" + ANSI_BLACK);
                }
            } while (opt != 5);
        } else if (LoginConsole.check == 2) {
            int opt = 0;
            // staff menu
            do {
                System.out.println("""
                           
                               MAIN MENU
                               1. Order / Checkout
                               2. Member Management
                               3. Product & Stock (view)
                               4. Reports
                               5. Log out
                               """);
                opt = readIntInRange("Option > ", 1, 5);

                switch (opt) {
                    case 1 -> OrderConsole.showMenu();
                    case 2 -> {
                        clearJavaConsoleScreen();
                        MemberConsole.showMenu();
                        clearJavaConsoleScreen();
                    }
                    case 3 -> ProductConsole.showMenu();
                    case 4 -> {
                        clearJavaConsoleScreen();
                        ReportConsole.showMenu();
                        clearJavaConsoleScreen();
                    }
                    case 5 -> {
                        logout();
                        return true;
                    }
                    default -> System.out.println(ANSI_RED + "Invalid input, please enter a number between 1 and 5 !!!" + ANSI_BLACK);
                }
            } while (opt != 5);
        }
        return true;
    }

    private static void logout() {
        currentUser = null;
        Cashier = null;
        check = 0;
        clearJavaConsoleScreen();
        System.exit(0);
    }
}
