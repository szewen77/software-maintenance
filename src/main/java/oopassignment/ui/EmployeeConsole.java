package oopassignment.ui;

import java.util.List;
import java.util.Optional;

import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.config.AppConfig;
import oopassignment.service.EmployeeService;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.*;

public class EmployeeConsole {

    private static final EmployeeService service = ApplicationContext.EMPLOYEE_SERVICE;

    public static void showMenu() {
        EmployeeRecord current = LoginConsole.getCurrentUser();
        if (current == null || current.getRole() != Role.MANAGER) {
            System.out.println(ANSI_RED + "Only managers can access employee management." + ANSI_BLACK);
            return;
        }

        int opt = 0;
        do {
            System.out.println("""

                    EMPLOYEE MENU
                    1. Display
                    2. Register
                    3. Search
                    4. Modify
                    5. Delete
                    6. Return to Main Menu
                    """);
            opt = readIntInRange("Option > ", 1, 6);
            switch (opt) {
                case 1 -> display();
                case 2 -> register();
                case 3 -> search();
                case 4 -> modify();
                case 5 -> deactivate();
                case 6 -> { }
                default -> System.out.println(
                        ANSI_RED + "Invalid input, please enter a number between 1 and 6 !!!" + ANSI_BLACK);
            }
        } while (opt != 6);
    }

    // ANSI Colors (optional — works in most terminals including VS Code)
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    private static void display() {
        List<EmployeeRecord> managers = service.listByRole(Role.MANAGER);
        List<EmployeeRecord> staff = service.listByRole(Role.STAFF);

        System.out.println();
        printSection("Managers", CYAN, managers);

        System.out.println();
        printSection("Staff", YELLOW, staff);
    }

    private static void printSection(String title, String color, List<EmployeeRecord> list) {
        // Table border
        String border = "+--------+--------------+------------+------------+------------+";

        // Header format
        String headerFmt = "| %-6s | %-12s | %-10s | %-10s | %-10s |%n";

        // Row format
        String rowFmt = "| %-6s | %-12s | %10.2f | %10.2f | %-10s |%n";

        // Print title in color
        System.out.println(color + title + RESET);
        System.out.println(border);
        System.out.printf(headerFmt, "ID", "Username", "Salary", "Bonus", "Status");
        System.out.println(border);

        // Print rows
        for (EmployeeRecord e : list) {
            System.out.printf(
                    rowFmt,
                    e.getId(),
                    e.getUsername(),
                    service.calculateSalary(e),
                    e.getBonusRate(),
                    e.getStatus());
        }

        // Bottom border
        System.out.println(border);
    }

    private static void register() {
        System.out.println("""

                REGISTER FUNCTION
                1. Add Manager
                2. Add Staff
                3. Back
                """);

        int add = readIntInRange("Add > ", 1, 3);
        if (add == 3) {
            return; // back to employee menu
        }

        Role role = (add == 1) ? Role.MANAGER : Role.STAFF;

        String username = readRequiredLine("Username: ");
        String password = readRequiredLine("Password: ");
        double baseSalary = readDouble("Base salary: ", 0.01);

        EmployeeRecord current = LoginConsole.getCurrentUser();
        String upline = null;
        if (role == Role.STAFF && current != null && current.getRole() == Role.MANAGER) {
            upline = current.getId();
        }

        // Confirm before creating
        System.out.printf("""

                Please confirm:
                Role       : %s
                Username   : %s
                Base salary: %.2f
                Upline     : %s
                """,
                role, username, baseSalary, (upline == null ? "-" : upline));

        if (!readYesNo("Confirm register? (Y/N): ")) {
            System.out.println("Registration cancelled.");
            return;
        }

        try {
            EmployeeRecord employee =
                    service.registerEmployee(username, password, role, baseSalary, upline);
            System.out.println("Registered: " + employee.getId());
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }


    private static void search() {
        System.out.print("Enter employee ID or username (" + AppConfig.BACK_TOKEN + " to return): ");
        String value = input.nextLine().trim();

        // User wants to go back
        if (value.equalsIgnoreCase(AppConfig.BACK_TOKEN)) {
            return;
        }
    
        Optional<EmployeeRecord> byId   = service.findById(value);
        Optional<EmployeeRecord> byUser = service.findByUsername(value);
        EmployeeRecord employee = byId.orElseGet(() -> byUser.orElse(null));
    
        if (employee == null) {
            System.out.println(ANSI_RED + "Employee not found" + ANSI_BLACK);
            return;
        }
    
        double salary = service.calculateSalary(employee);
    
        String border   = "+--------+--------------+----------------------+------------+------------+";
        String headerFm = "| %-6s | %-12s |  %-10s | %-10s |%n";
        String rowFm    = "| %-6s | %-12s |  %10.2f | %-10s |%n";
    
        System.out.println();
        System.out.println("Employee Details");
        System.out.println(border);
        System.out.printf(headerFm, "ID", "Username", "Salary", "Bonus", "Status");
        System.out.println(border);
        System.out.printf(
                rowFm,
                employee.getId(),
                employee.getUsername(),
                salary,
                employee.getBonusRate(),
                employee.getStatus()
        );
        System.out.println(border);
    
        System.out.printf("Role : %s%n", employee.getRole());
        System.out.printf("Base : %.2f%n", employee.getBaseSalary());
        System.out.printf("Upline: %s%n", employee.getUplineId() == null ? "-" : employee.getUplineId());
    }
    

    

    private static void modify() {
        System.out.print("Enter employee ID to modify (" + AppConfig.BACK_TOKEN + " to return): ");
        String id = input.nextLine().trim();

        if (id.equalsIgnoreCase(AppConfig.BACK_TOKEN)) {
            return;
        }

        Optional<EmployeeRecord> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            System.out.println(ANSI_RED + "Employee not found" + ANSI_BLACK);
            return;
        }

        EmployeeRecord existing = existingOpt.get();

        System.out.println("Current values:");
        System.out.println("Username   : " + existing.getUsername());
        System.out.println("Role       : " + existing.getRole());
        System.out.println("Base salary: " + existing.getBaseSalary());
        System.out.println("Upline     : " + (existing.getUplineId() == null ? "-" : existing.getUplineId()));
        System.out.println();

        String roleInput = readOptionalLine("Role (MANAGER/STAFF, blank = keep): ");
        Role role;
        if (roleInput == null) {
            role = existing.getRole();
        } else {
            try {
                role = Role.valueOf(roleInput.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println(ANSI_RED + "Invalid role. Keeping existing role." + ANSI_BLACK);
                role = existing.getRole();
            }
        }

        Double salaryInput = readOptionalDouble("Base salary (blank = keep): ", 0.0);
        double baseSalary = salaryInput == null ? existing.getBaseSalary() : salaryInput;

        // Auto-manage upline based on role rules
        String upline = existing.getUplineId();
        if (role == Role.MANAGER) {
            upline = null;
        }

        // Confirm
        System.out.printf("""

                New values:
                Role       : %s
                Base salary: %.2f
                Upline     : %s
                """,
                role, baseSalary, (upline == null ? "-" : upline));
        if (!readYesNo("Confirm update? (Y/N): ")) {
            System.out.println("Update cancelled.");
            return;
        }

        try {
            EmployeeRecord updated = service.modifyEmployee(id, null, role, baseSalary, upline);
            System.out.println("Updated " + updated.getId());
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }


    private static void deactivate() {
        System.out.print("Enter employee ID to deactivate (" + AppConfig.BACK_TOKEN + " to return): ");
        String id = input.nextLine().trim();

        if (id.equalsIgnoreCase(AppConfig.BACK_TOKEN)) {
            return;
        }

        EmployeeRecord current = LoginConsole.getCurrentUser();
        if (current != null && current.getId().equals(id)) {
            System.out.println(ANSI_RED + "You cannot deactivate yourself." + ANSI_BLACK);
            return;
        }

        Optional<EmployeeRecord> targetOpt = service.findById(id);
        if (targetOpt.isEmpty()) {
            System.out.println(ANSI_RED + "Employee not found." + ANSI_BLACK);
            return;
        }

        EmployeeRecord target = targetOpt.get();
        System.out.printf("Deactivate %s (%s)? (Y/N): %n",
                target.getId(), target.getUsername());
        String confirm = input.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deactivation cancelled.");
            return;
        }

        try {
            service.deactivateEmployee(id, current == null ? "" : current.getId());
            System.out.println("Deactivated " + id);
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }

        clearJavaConsoleScreen();
    }

}
