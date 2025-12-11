package oopassignment.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.StockItem;
import oopassignment.domain.member.MemberRecord;
import oopassignment.service.InventoryService;
import oopassignment.service.OrderService;
import oopassignment.service.ProductService;
import oopassignment.service.MemberService;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.*;

public class OrderConsole {

    private static final ProductService productService = ApplicationContext.PRODUCT_SERVICE;
    private static final InventoryService inventoryService = ApplicationContext.INVENTORY_SERVICE;
    private static final OrderService orderService = ApplicationContext.ORDER_SERVICE;
    private static final MemberService memberService = ApplicationContext.MEMBER_SERVICE;

    
    // Simple alias to keep console navigation consistent
    public static void showMenu() {
        startOrderFlow();
    }

    public static void startOrderFlow() {
        List<ProductRecord> products = productService.findAll();
        if (products.isEmpty()) {
            System.out.println("No products available to order.");
            return;
        }
        System.out.println("Available products:");
        for (ProductRecord p : products) {
            int totalQty = inventoryService.getStockForProduct(p.getProductId()).stream()
                    .mapToInt(StockItem::getQuantity)
                    .sum();
            System.out.printf("%s - %s (%s) RM%.2f | Stock: %d%n",
                    p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), totalQty);
        }

        // Validate member ID if provided
        String memberId = null;
        MemberRecord member = null;
        while (true) {
            String inputMemberId = readOptionalLine("Member ID (blank if non-member): ");
            if (inputMemberId == null || inputMemberId.isBlank()) {
                // No member, continue
                break;
            }
            Optional<MemberRecord> memberOpt = memberService.findById(inputMemberId);
            if (memberOpt.isPresent()) {
                member = memberOpt.get();
                memberId = inputMemberId;
                System.out.println(ANSI_GREEN + "Member found: " + member.getName() + ANSI_BLACK);
                break;
            } else {
                System.out.println(ANSI_RED + "Member ID not found. Please try again or leave blank." + ANSI_BLACK);
            }
        }

        String customerId = readOptionalLine("Customer ID (blank to auto-generate walk-in): ");

        List<OrderItemRequest> items = new ArrayList<>();
        boolean more;
        do {
            String productId = readRequiredLine("Enter product id: ");
            if (productService.findById(productId).isEmpty()) {
                System.out.println(ANSI_RED + "Product not found." + ANSI_BLACK);
            } else {
                String size = readRequiredLine("Enter size: ");
                int qty = readPositiveInt("Enter quantity: ");

                if (!inventoryService.isStockAvailable(productId, size, qty)) {
                    System.out.println(ANSI_RED + "Not enough stock for " + productId + " size " + size + ANSI_BLACK);
                } else {
                    items.add(new OrderItemRequest(productId, size, qty));
                }
            }
            more = readYesNo("Add another item? (Y/N): ");
        } while (more);

        if (items.isEmpty()) {
            System.out.println(ANSI_RED + "No items in order" + ANSI_BLACK);
            return;
        }

        // Calculate order total before payment
        double orderTotal = calculateOrderTotal(items, memberId != null);

        String payment = readPaymentMethod(member, orderTotal);

        try {
            OrderRequest request = new OrderRequest(
                    memberId == null || memberId.isBlank() ? null : memberId,
                    customerId == null || customerId.isBlank() ? "CU-WALKIN" : customerId,
                    items,
                    payment
            );
            OrderResult result = orderService.placeOrder(request);
            clearJavaConsoleScreen();
            printReceipt(result);
           
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }

    private static void printReceipt(OrderResult result) {
        String border = "+----------------+------------+";
        String lineFm = "| %-14s | %10.2f |%n";
    
        System.out.println();
        System.out.println("Transaction " + result.getTransactionId());
        System.out.println(border);
        System.out.printf(lineFm, "Subtotal", result.getSubtotal());
        System.out.printf(lineFm, "Discount", result.getDiscount());
        System.out.printf(lineFm, "Total",    result.getTotal());
        System.out.println(border);
    }
    
    private static double calculateOrderTotal(List<OrderItemRequest> items, boolean isMember) {
        double subtotal = 0;
        for (OrderItemRequest item : items) {
            Optional<ProductRecord> productOpt = productService.findById(item.getProductId());
            if (productOpt.isPresent()) {
                subtotal += productOpt.get().getPrice() * item.getQuantity();
            }
        }
        // Apply 5% member discount if applicable
        if (isMember) {
            subtotal = subtotal * 0.95;
        }
        return subtotal;
    }

    private static String readPaymentMethod(MemberRecord member, double orderTotal) {
        while (true) {
            String prompt;
            if (member != null) {
                prompt = "Payment method (CASH/CARD/WALLET): ";
            } else {
                prompt = "Payment method (CASH/CARD): ";
            }
            String method = readRequiredLine(prompt).toUpperCase();
            
            if ("CASH".equals(method) || "CARD".equals(method)) {
                return method;
            }
            
            if ("WALLET".equals(method)) {
                if (member == null) {
                    System.out.println(ANSI_RED + "Wallet payment is only available for members." + ANSI_BLACK);
                    continue;
                }
                
                // Show current balance
                System.out.printf(ANSI_CYAN + "Current Boost Wallet Balance: RM%.2f%n" + ANSI_BLACK, member.getCreditBalance());
                System.out.printf("Order Total: RM%.2f%n", orderTotal);
                
                // Check if balance is sufficient
                if (member.getCreditBalance() >= orderTotal) {
                    System.out.println(ANSI_GREEN + "Sufficient balance available!" + ANSI_BLACK);
                    return "WALLET";
                } else {
                    double shortfall = orderTotal - member.getCreditBalance();
                    System.out.printf(ANSI_YELLOW + "Insufficient balance. Short by: RM%.2f%n" + ANSI_BLACK, shortfall);
                    
                    // Offer top-up
                    while (true) {
                        String topUpInput = readOptionalLine("Enter amount to top up (or X to go back): ");
                        if ("X".equalsIgnoreCase(topUpInput)) {
                            System.out.println(ANSI_YELLOW + "Returning to payment method selection..." + ANSI_BLACK);
                            break;
                        }
                        
                        try {
                            double topUpAmount = Double.parseDouble(topUpInput);
                            if (topUpAmount <= 0) {
                                System.out.println(ANSI_RED + "Top-up amount must be greater than 0." + ANSI_BLACK);
                                continue;
                            }
                            
                            // Top up the member
                            MemberRecord updatedMember = memberService.topUpCredit(member.getMemberId(), topUpAmount);
                            member.setCreditBalance(updatedMember.getCreditBalance());
                            System.out.printf(ANSI_GREEN + "Top-up successful! New balance: RM%.2f%n" + ANSI_BLACK, member.getCreditBalance());
                            
                            // Check again if balance is now sufficient
                            if (member.getCreditBalance() >= orderTotal) {
                                System.out.println(ANSI_GREEN + "Balance is now sufficient!" + ANSI_BLACK);
                                return "WALLET";
                            } else {
                                double remainingShortfall = orderTotal - member.getCreditBalance();
                                System.out.printf(ANSI_YELLOW + "Still short by: RM%.2f%n" + ANSI_BLACK, remainingShortfall);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(ANSI_RED + "Invalid amount. Please enter a valid number or X to cancel." + ANSI_BLACK);
                        }
                    }
                }
            } else {
                if (member != null) {
                    System.out.println(ANSI_RED + "Supported methods: CASH, CARD, or WALLET." + ANSI_BLACK);
                } else {
                    System.out.println(ANSI_RED + "Supported methods: CASH or CARD." + ANSI_BLACK);
                }
            }
        }
    }
}
