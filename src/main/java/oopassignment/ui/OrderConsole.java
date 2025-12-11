package oopassignment.ui;

import java.util.ArrayList;
import java.util.List;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.StockItem;
import oopassignment.service.InventoryService;
import oopassignment.service.OrderService;
import oopassignment.service.ProductService;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.*;

public class OrderConsole {

    private static final ProductService productService = ApplicationContext.PRODUCT_SERVICE;
    private static final InventoryService inventoryService = ApplicationContext.INVENTORY_SERVICE;
    private static final OrderService orderService = ApplicationContext.ORDER_SERVICE;

    
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

        String memberId = readOptionalLine("Member ID (blank if non-member): ");
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

        String payment = readPaymentMethod();

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
    
    private static String readPaymentMethod() {
        while (true) {
            String method = readRequiredLine("Payment method (CASH/CARD): ").toUpperCase();
            if ("CASH".equals(method) || "CARD".equals(method)) {
                return method;
            }
            System.out.println(ANSI_RED + "Supported methods: CASH or CARD." + ANSI_BLACK);
        }
    }
}
