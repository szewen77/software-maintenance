package oopassignment.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oopassignment.domain.product.ProductRecord;
import oopassignment.domain.product.StockItem;
import oopassignment.config.AppConfig;
import oopassignment.service.InventoryService;
import oopassignment.service.ProductService;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.*;

public class ProductConsole {

    private static final List<String> CLOTHES_SIZES = List.of("XS", "S", "M", "L", "XL");
    private static final List<String> SHOE_SIZES = List.of("34", "36", "38", "40", "42");
    private static final List<String> ALLOWED_CATEGORIES = List.of("clothes", "shoes");

    private static final ProductService productService = ApplicationContext.PRODUCT_SERVICE;
    private static final InventoryService inventoryService = ApplicationContext.INVENTORY_SERVICE;

    public static void showMenu() {
        int choice;
        do {
            displayProducts();
            System.out.println("""
                    =================
                    1 = Add New Product
                    2 = Delete Product
                    3 = Replenish Stock
                    4 = Return to Main Menu
                    """);
            choice = readIntInRange("Option > ", 1, 4);

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> deleteProduct();
                case 3 -> replenishStock();
                case 4 -> { }
                default -> System.out.println("Invalid input, please enter a number between 1 and 4");
            }
        } while (choice != 4);
    }
    

    private static void addProduct() {
        String name = readRequiredLine("Product name: ");
        String category;
        while (true) {
            category = readRequiredLine("Category (clothes/shoes): ").toLowerCase();
            if (ALLOWED_CATEGORIES.contains(category)) {
                break;
            }
            System.out.println(ANSI_RED + "Invalid category. Allowed: clothes, shoes." + ANSI_BLACK);
        }
        double price = readDouble("Price: ", 0.01);
        try {
            ProductRecord product = productService.addProduct(name, category, price);
            System.out.println("Added product with id " + product.getProductId());

            // Offer to add initial stock immediately
            if (readYesNo("Add initial stock now (Y/N)? ")) {
                List<String> allowedSizes = category.equals("clothes") ? CLOTHES_SIZES : SHOE_SIZES;
                String size = null;
                while (true) {
                    String candidate = readRequiredLine("Size (" + String.join(", ", allowedSizes) + "): ");
                    if (allowedSizes.stream().anyMatch(s -> s.equalsIgnoreCase(candidate))) {
                        for (String s : allowedSizes) {
                            if (s.equalsIgnoreCase(candidate)) {
                                size = s;
                                break;
                            }
                        }
                        break;
                    }
                    System.out.println(ANSI_RED + "Invalid size. Allowed: " + String.join(", ", allowedSizes) + ANSI_BLACK);
                }
                int qty = readPositiveInt("Quantity: ");
                inventoryService.increaseStock(product.getProductId(), size, qty);
                System.out.printf("Stock updated for %s size %s = %d%n", product.getProductId(), size, qty);
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }

    private static void deleteProduct() {
        String id = readRequiredLine("Enter product id to delete (" + AppConfig.BACK_TOKEN + " to cancel): ");
        if (id.equalsIgnoreCase(AppConfig.BACK_TOKEN)) {
            return;
        }
        try {
            productService.deleteProduct(id);
            System.out.println("Deleted " + id);
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }


    private static void replenishStock() {
        // 1. Ask for product ID and validate it exists
        String id = readRequiredLine("Product ID: ");
        var productOpt = productService.findById(id);
    
        if (productOpt.isEmpty()) {
            System.out.println(ANSI_RED + "Product " + id + " not found." + ANSI_BLACK);
            return;
        }
    
        var product = productOpt.get();
        String category = product.getCategory(); // adjust if your getter name is different
    
        boolean isClothes = category != null && category.equalsIgnoreCase("clothes");
        boolean isShoes   = category != null && category.equalsIgnoreCase("shoes");
    
        // 2. Load existing stock rows from DB
        List<StockItem> dbStock = inventoryService.getStockForProduct(id);
    
        // 3. Build a size → qty map based on category defaults
        LinkedHashMap<String, Integer> sizeQty = new LinkedHashMap<>();
    
        if (isClothes) {
            for (String s : CLOTHES_SIZES) {
                sizeQty.put(s, 0);
            }
        } else if (isShoes) {
            for (String s : SHOE_SIZES) {
                sizeQty.put(s, 0);
            }
        }
    
        // If category is unknown or something else, just use whatever is in DB
        if (!isClothes && !isShoes) {
            for (StockItem s : dbStock) {
                sizeQty.put(s.getSize(), s.getQuantity());
            }
        } else {
            // For clothes/shoes, override default 0 with real DB quantities
            for (StockItem s : dbStock) {
                String size = s.getSize();
                if (sizeQty.containsKey(size)) {
                    sizeQty.put(size, s.getQuantity());
                } else {
                    // in case DB has extra weird size not in default list, still show it
                    sizeQty.put(size, s.getQuantity());
                }
            }
        }
    
        // 4. Show current stock table (including 0-qty sizes)
        String border   = "+----------+--------+----------+";
        String headerFm = "| %-8s | %-6s | %-8s |%n";
        String rowFm    = "| %-8s | %-6s | %8d |%n";
    
        System.out.println();
        System.out.println("Current stock for " + id + " (" + product.getName() + ")");
        System.out.println(border);
        System.out.printf(headerFm, "Product", "Size", "Qty");
        System.out.println(border);
    
        for (var entry : sizeQty.entrySet()) {
            System.out.printf(rowFm, id, entry.getKey(), entry.getValue());
        }
    
        System.out.println(border);
        System.out.println();
    
        // 5. Decide allowed sizes for input
        List<String> allowedSizes;
        if (isClothes) {
            allowedSizes = CLOTHES_SIZES;
        } else if (isShoes) {
            allowedSizes = SHOE_SIZES;
        } else {
            // fallback: whatever sizes we have
            allowedSizes = new ArrayList<>(sizeQty.keySet());
        }
    
        // 6. Ask which size to replenish – enforce valid list
        String size = null;
        while (true) {
            String candidate = readRequiredLine("Size to replenish (available: "
                    + String.join(", ", allowedSizes) + "): ");
            if (allowedSizes.stream().anyMatch(s -> s.equalsIgnoreCase(candidate))) {
                // normalize to exactly the configured case
                for (String s : allowedSizes) {
                    if (s.equalsIgnoreCase(candidate)) {
                        size = s;
                        break;
                    }
                }
                break;
            }
            System.out.println(ANSI_RED + "Invalid size. Allowed sizes: "
                    + String.join(", ", allowedSizes) + ANSI_BLACK);
        }
    
        int qty = readPositiveInt("Quantity to add: ");
    
        // 7. Confirm before applying
        System.out.printf("Confirm add %d units of %s (size %s)? (Y/N): ",
                qty, product.getName(), size);
        String confirm = input.nextLine().trim();
    
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Replenish cancelled.");
            return;
        }
    
        // 8. Update DB and show new quantity
        final String chosenSize = size;
        try {
            inventoryService.increaseStock(id, size, qty);
    
            // Re-fetch that size to show new quantity
            List<StockItem> updatedStock = inventoryService.getStockForProduct(id);
            int newQty = updatedStock.stream()
                    .filter(s -> s.getSize().equalsIgnoreCase(chosenSize))
                    .mapToInt(StockItem::getQuantity)
                    .findFirst()
                    .orElse(0);
    
            System.out.println("Stock updated. New quantity for size " + chosenSize + " = " + newQty);
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }
    
    

    private static void displayProducts() {
        List<ProductRecord> products = productService.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
    
        System.out.println();
        System.out.println(ANSI_CYAN + "Products & Stock Details" + ANSI_BLACK);
        System.out.println();
    
        // Single table for all products
        String border = "+----------+----------------------+------------+------------+--------+----------+";
        String headerFm = "| %-8s | %-20s | %-10s | %-10s | %-6s | %-8s |%n";
        String rowFm = "| %-8s | %-20s | %10.2f | %-10s | %-6s | %8d |%n";
        String emptyRowFm = "| %-8s | %-20s | %10s | %-10s | %-6s | %8d |%n";
        
        System.out.println(border);
        System.out.printf(headerFm, "ID", "Name", "Price", "Category", "Size", "Quantity");
        System.out.println(border);
        
        for (ProductRecord p : products) {
            List<StockItem> stockItems = inventoryService.getStockForProduct(p.getProductId());
            
            // Sort sizes for better readability
            List<StockItem> sortedStock = new ArrayList<>(stockItems);
            sortedStock.sort((a, b) -> {
                String sizeA = a.getSize();
                String sizeB = b.getSize();
                int indexA = CLOTHES_SIZES.indexOf(sizeA);
                int indexB = CLOTHES_SIZES.indexOf(sizeB);
                if (indexA != -1 && indexB != -1) {
                    return Integer.compare(indexA, indexB);
                }
                if (indexA != -1) return -1;
                if (indexB != -1) return 1;
                indexA = SHOE_SIZES.indexOf(sizeA);
                indexB = SHOE_SIZES.indexOf(sizeB);
                if (indexA != -1 && indexB != -1) {
                    return Integer.compare(indexA, indexB);
                }
                return sizeA.compareTo(sizeB);
            });
            
            if (sortedStock.isEmpty()) {
                // Product with no stock - show product info with N/A for size and 0 for quantity
                System.out.printf(rowFm, 
                    p.getProductId(), 
                    p.getName(), 
                    p.getPrice(), 
                    p.getCategory(),
                    "N/A",
                    0
                );
            } else {
                // First row: show product info with first size
                StockItem firstStock = sortedStock.get(0);
                System.out.printf(rowFm, 
                    p.getProductId(), 
                    p.getName(), 
                    p.getPrice(), 
                    p.getCategory(),
                    firstStock.getSize(),
                    firstStock.getQuantity()
                );
                
                // Subsequent rows: show only sizes (empty product info columns)
                for (int i = 1; i < sortedStock.size(); i++) {
                    StockItem stock = sortedStock.get(i);
                    System.out.printf(emptyRowFm, 
                        "",  // Empty ID
                        "",  // Empty Name
                        "",  // Empty Price
                        "",  // Empty Category
                        stock.getSize(),
                        stock.getQuantity()
                    );
                }
            }
        }
        
        System.out.println(border);
    }


    private static void displayStock() {
        displayProducts();
    }

    private static void viewStock() {
        String id = readRequiredLine("Product id: ");
    
        List<StockItem> stockItems = inventoryService.getStockForProduct(id);
        if (stockItems.isEmpty()) {
            System.out.println("No stock for product " + id);
            return;
        }
    
        String border   = "+----------+--------+----------+";
        String headerFm = "| %-8s | %-6s | %-8s |%n";
        String rowFm    = "| %-8s | %-6s | %8d |%n";
    
        System.out.println();
        System.out.println("Stock for " + id);
        System.out.println(border);
        System.out.printf(headerFm, "Product", "Size", "Qty");
        System.out.println(border);
    
        for (StockItem s : stockItems) {
            System.out.printf(rowFm, s.getProductId(), s.getSize(), s.getQuantity());
        }
    
        System.out.println(border);
    }
    
    
}
