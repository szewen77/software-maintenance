package oopassignment.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import oopassignment.service.EmployeeService;
import oopassignment.service.ReportService;
import oopassignment.service.ProductService;
import oopassignment.repository.TransactionRepository;
import oopassignment.util.ApplicationContext;
import java.util.LinkedHashMap;
import java.util.Map;

import static oopassignment.ui.BootsDo.*;

public class ReportConsole {

    private static final ReportService reportService = ApplicationContext.REPORT_SERVICE;
    private static final EmployeeService employeeService = ApplicationContext.EMPLOYEE_SERVICE;
    private static final ProductService productService = ApplicationContext.PRODUCT_SERVICE;
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
    
        System.out.println();
        System.out.println(ANSI_CYAN + "Purchase history for " + memberId + ANSI_BLACK);
        System.out.println();
    
        // Single table for all purchases and items
        String border = "+---------------------+------------+------------+----------------------+--------+----------+------------+------------+";
        String headerFm = "| %-19s | %-10s | %-10s | %-20s | %-6s | %-8s | %-10s | %-10s |%n";
        String rowFm = "| %-19s | %10.2f | %-10s | %-20s | %-6s | %8d | %10.2f | %10.2f |%n";
        String emptyRowFm = "| %-19s | %10s | %-10s | %-20s | %-6s | %8d | %10.2f | %10.2f |%n";
        
        System.out.println(border);
        System.out.printf(headerFm, "DateTime", "Amount", "Payment", "Product Name", "Size", "Quantity", "Unit Price", "Line Total");
        System.out.println(border);
    
        // Get transaction repository to fetch items
        var transactionRepo = ApplicationContext.TRANSACTION_REPOSITORY;
        
        for (MemberPurchase p : purchases) {
            String formattedDateTime = p.getDateTime().format(DATETIME_FORMATTER);
            
            // Fetch transaction items
            List<TransactionItem> items = transactionRepo.findItemsByTransaction(p.getTransactionId());
            
            if (items.isEmpty()) {
                // Transaction with no items - show transaction info only
                System.out.printf(rowFm,
                    formattedDateTime,
                    p.getAmount(),
                    p.getPaymentMethod(),
                    "N/A",
                    "N/A",
                    0,
                    0.0,
                    0.0
                );
            } else {
                // First row: show transaction info with first item
                TransactionItem firstItem = items.get(0);
                Optional<ProductRecord> productOpt = productService.findById(firstItem.getProductId());
                String productName = productOpt.map(ProductRecord::getName).orElse(firstItem.getProductId());
                double lineTotal = firstItem.getUnitPrice() * firstItem.getQuantity();
                
                System.out.printf(rowFm,
                    formattedDateTime,
                    p.getAmount(),
                    p.getPaymentMethod(),
                    productName,
                    firstItem.getSize(),
                    firstItem.getQuantity(),
                    firstItem.getUnitPrice(),
                    lineTotal
                );
                
                // Subsequent rows: show only items (empty transaction info columns)
                for (int i = 1; i < items.size(); i++) {
                    TransactionItem item = items.get(i);
                    productOpt = productService.findById(item.getProductId());
                    productName = productOpt.map(ProductRecord::getName).orElse(item.getProductId());
                    lineTotal = item.getUnitPrice() * item.getQuantity();
                    
                    System.out.printf(emptyRowFm,
                        "",  // Empty DateTime
                        "",  // Empty Amount
                        "",  // Empty Payment
                        productName,
                        item.getSize(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        lineTotal
                    );
                }
            }
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
        String border = "+------------+---------------------+------------+-----------------+------------+------------+";
        String headerFm = "| %-10s | %-19s | %-10s | %-15s | %-10s | %10s |%n";
        String rowFm    = "| %-10s | %-19s | %-10s | %-15s | %-10s | %10.2f |%n";
    
        System.out.println();
        System.out.println(ANSI_CYAN + "Sales Summary: " + from + " to " + to + ANSI_BLACK);
        System.out.println();
        System.out.println("Transaction Details:");
        System.out.println(border);
        System.out.printf(headerFm, "Trans ID", "DateTime", "Member ID", "Customer Type", "Payment", "Amount");
        System.out.println(border);
    
        for (TransactionHeader t : transactions) {
            String formattedDateTime = t.getDateTime().format(DATETIME_FORMATTER);
            String memberId = t.getMemberId() != null && !t.getMemberId().isBlank() ? t.getMemberId() : "N/A";
            String customerType = t.getCustomerType() != null && !t.getCustomerType().isBlank() ? t.getCustomerType() : "N/A";
            String paymentMethod = t.getPaymentMethod() != null && !t.getPaymentMethod().isBlank() ? t.getPaymentMethod() : "N/A";
            System.out.printf(
                rowFm,
                t.getTransactionId(),
                formattedDateTime,
                memberId,
                customerType,
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
