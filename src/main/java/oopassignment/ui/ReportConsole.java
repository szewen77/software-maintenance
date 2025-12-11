package oopassignment.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import oopassignment.service.EmployeeService;
import oopassignment.service.ReportService;
import oopassignment.util.ApplicationContext;
import java.util.LinkedHashMap;
import java.util.Map;

import static oopassignment.ui.BootsDo.*;

public class ReportConsole {

    private static final ReportService reportService = ApplicationContext.REPORT_SERVICE;
    private static final EmployeeService employeeService = ApplicationContext.EMPLOYEE_SERVICE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void showMenu() {
        int opt = 0;
        do {
            System.out.println("""
                              
                               Report Menu
                               1. Employee salary
                               2. Member purchase history
                               3. Sales summary
                               4. Staff by manager
                               5. Return to Main Menu
                               """);
            opt = readIntInRange("Option > ", 1, 5);
            switch (opt) {
                case 1 -> employeeSalary();
                case 2 -> memberPurchase();
                case 3 -> salesSummary();
                case 4 -> staffByManager();
                case 5 -> { }
                default -> System.out.println(ANSI_RED + "Invalid input, please enter a number between 1 and 5 !!!" + ANSI_BLACK);
            }
        } while (opt != 5);
    }
    

    private static void employeeSalary() {
        Role role = readRole();
        double min = readDouble("Min salary: ", 0.0);
        double max = readDouble("Max salary: ", min);
    
        List<EmployeeRecord> list;
        try {
            list = employeeService.listByRole(role);
        } catch (Exception ex) {
            System.out.println(ANSI_RED + ex.getMessage() + ANSI_BLACK);
            return;
        }
    
        String border   = "+--------+--------------+------------+";
        String headerFm = "| %-6s | %-12s | %-10s |%n";
        String rowFm    = "| %-6s | %-12s | %10.2f |%n";
    
        System.out.println();
        System.out.println("Employees in range");
        System.out.println(border);
        System.out.printf(headerFm, "ID", "Username", "Salary");
        System.out.println(border);
    
        boolean any = false;
        for (EmployeeRecord e : list) {
            double salary = employeeService.calculateSalary(e);
            if (salary >= min && salary <= max) {
                any = true;
                System.out.printf(
                        rowFm,
                        e.getId(),
                        e.getUsername(),
                        salary
                );
            }
        }
        if (!any) {
            System.out.println("(No employees in range)");
        }
    
        System.out.println(border);
    }
    

    private static void staffByManager() {
        List<EmployeeRecord> managers;
        List<EmployeeRecord> staff;
        try {
            managers = employeeService.listByRole(Role.MANAGER);
            staff = employeeService.listByRole(Role.STAFF);
        } catch (Exception ex) {
            System.out.println(ANSI_RED + ex.getMessage() + ANSI_BLACK);
            return;
        }

        Map<String, String> managerLabels = new LinkedHashMap<>();
        for (EmployeeRecord m : managers) {
            managerLabels.put(m.getId(), m.getId() + " (" + m.getUsername() + ")");
        }

        String border   = "+----------------------+----------+--------------+";
        String headerFm = "| %-20s | %-8s | %-12s |%n";
        String rowFm    = "| %-20s | %-8s | %-12s |%n";

        System.out.println();
        System.out.println("Staff by Manager");
        System.out.println(border);
        System.out.printf(headerFm, "Manager", "Staff ID", "Username");
        System.out.println(border);

        if (staff.isEmpty()) {
            System.out.println("(No staff found)");
            System.out.println(border);
            return;
        }

        for (EmployeeRecord s : staff) {
            String mgrId = s.getUplineId();
            String label;
            if (mgrId == null || mgrId.isBlank()) {
                label = "Unassigned";
            } else {
                label = managerLabels.getOrDefault(mgrId, mgrId + " (unknown)");
            }
            System.out.printf(rowFm, label, s.getId(), s.getUsername());
        }

        System.out.println(border);
    }

    private static void memberPurchase() {
        String memberId = readRequiredLine("Member ID: ");
        List<MemberPurchase> purchases;
        try {
            purchases = reportService.getMemberPurchaseHistory(memberId);
        } catch (Exception ex) {
            System.out.println(ANSI_RED + ex.getMessage() + ANSI_BLACK);
            return;
        }
    
        if (purchases.isEmpty()) {
            System.out.println("No purchases");
            return;
        }
    
        String border   = "+---------------------+------------+------------+";
        String headerFm = "| %-19s | %-10s | %-10s |%n";
        String rowFm    = "| %-19s | %10.2f | %-10s |%n";
    
        System.out.println();
        System.out.println("Purchase history for " + memberId);
        System.out.println(border);
        System.out.printf(headerFm, "DateTime", "Amount", "Payment");
        System.out.println(border);
    
        for (MemberPurchase p : purchases) {
            String formattedDateTime = p.getDateTime().format(DATETIME_FORMATTER);
            System.out.printf(
                rowFm,
                formattedDateTime,
                p.getAmount(),
                p.getPaymentMethod()
                //"N/A"
            );
        }
    
        System.out.println(border);
        }
    

    private static void salesSummary() {
        LocalDate from = readDateStrict("From (yyyy-MM-dd): ");
        LocalDate to = readDateStrict("To   (yyyy-MM-dd): ");
        if (to.isBefore(from)) {
            System.out.println(ANSI_RED + "End date must not be earlier than start date." + ANSI_BLACK);
            return;
        }
        
        List<TransactionHeader> transactions;
        SalesSummary summary;
        try {
            transactions = reportService.getTransactionsInRange(from, to);
            summary = reportService.getSalesSummary(from, to);
        } catch (Exception ex) {
            System.out.println(ANSI_RED + ex.getMessage() + ANSI_BLACK);
            return;
        }
    
        if (transactions.isEmpty()) {
            System.out.println(ANSI_YELLOW + "\nNo transactions found for the selected date range." + ANSI_BLACK);
            return;
        }
    
        // Display detailed transaction list
        String border = "+------------+---------------------+------------+------------+------------+------------+";
        String headerFm = "| %-10s | %-19s | %-10s | %-10s | %-10s | %10s |%n";
        String rowFm    = "| %-10s | %-19s | %-10s | %-10s | %-10s | %10.2f |%n";
    
        System.out.println();
        System.out.println(ANSI_CYAN + "Sales Summary: " + from + " to " + to + ANSI_BLACK);
        System.out.println();
        System.out.println("Transaction Details:");
        System.out.println(border);
        System.out.printf(headerFm, "Trans ID", "DateTime", "Member ID", "Customer ID", "Payment", "Amount");
        System.out.println(border);
    
        for (TransactionHeader t : transactions) {
            String formattedDateTime = t.getDateTime().format(DATETIME_FORMATTER);
            String memberId = t.getMemberId() != null && !t.getMemberId().isBlank() ? t.getMemberId() : "N/A";
            String customerId = t.getCustomerId() != null && !t.getCustomerId().isBlank() ? t.getCustomerId() : "N/A";
            String paymentMethod = t.getPaymentMethod() != null && !t.getPaymentMethod().isBlank() ? t.getPaymentMethod() : "N/A";
            System.out.printf(
                rowFm,
                t.getTransactionId(),
                formattedDateTime,
                memberId,
                customerId,
                paymentMethod,
                t.getTotalAmount()
            );
        }
        System.out.println(border);
        
        // Display summary section
        System.out.println();
        System.out.println(ANSI_CYAN + "Summary:" + ANSI_BLACK);
        String summaryBorder = "+--------------------------------+------------+------------+------------+";
        String summaryHeaderFm = "| %-30s | %-10s | %-10s | %-10s |%n";
        String summaryRowFm    = "| %-30s | %10.2f | %10d | %10.2f |%n";
        
        System.out.println(summaryBorder);
        System.out.printf(summaryHeaderFm, "Period", "Total", "Count", "Avg");
        System.out.println(summaryBorder);
        System.out.printf(
            summaryRowFm,
            from + " to " + to,
            summary.getTotalAmount(),
            summary.getTransactionCount(),
            summary.getAveragePerTransaction()
        );
        System.out.println(summaryBorder);
    }
    

    private static LocalDate readDateStrict(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readRequiredLine(prompt));
            } catch (DateTimeParseException ex) {
                System.out.println(ANSI_RED + "Invalid date format (expected yyyy-MM-dd)." + ANSI_BLACK);
            }
        }
    }

    private static Role readRole() {
        while (true) {
            String roleStr = readRequiredLine("Role (MANAGER/STAFF): ");
            try {
                return Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println(ANSI_RED + "Unknown role. Please enter MANAGER or STAFF." + ANSI_BLACK);
            }
        }
    }
}
