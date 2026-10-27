import java.util.List;
import java.util.Scanner;

/**
 * Bookstore Console Application (Text-Based)
 * Clean, easy to understand implementation demonstrating OOP principles:
 * - Abstraction
 * - Encapsulation
 * - Inheritance
 * - Polymorphism (Compile-time & Runtime)
 * - Static vs Non-Static
 * - Method Overloading & Overriding
 * - 'this' and 'super' keywords
 * - Subclass Reference vs Superclass Reference
 * - Diamond Problem Resolution
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Bookstore store = new Bookstore("bookstore_data.txt");

    public static void main(String[] args) {
        // Pre-seed sample products if inventory is empty
        if (store.getInventory().isEmpty()) {
            store.addProduct(new Product.Book("B001", "Clean Code", 650.00, 10, "Robert C. Martin", "978-0132350884"));
            store.addProduct(new Product.Book("B002", "Effective Java", 850.00, 8, "Joshua Bloch", "978-0134685991"));
            store.addProduct(new Product.Stationery("S001", "Gel Pen Pack (Blue)", 120.00, 25, "Pilot"));
        }

        System.out.println("====================================================================");
        System.out.println("                   WELCOME TO JAVA BOOKSTORE                        ");
        System.out.println("====================================================================");

        while (true) {
            printMenu();
            System.out.print("Enter choice (0-8 or command): ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1", "inventory", "inv" -> showInventory();
                case "2", "add", "a"         -> addProduct();
                case "3", "restock", "r"     -> restockProduct();
                case "4", "price"            -> changePrice();
                case "5", "sell", "checkout" -> checkout();
                case "6", "sales"            -> showSalesHistory();
                case "7", "reports", "rep"   -> showReports();
                case "8", "demo", "oop"      -> demonstrateOOP();
                case "0", "exit", "quit"     -> {
                    System.out.println("\nThank you for using Java Bookstore. All data is saved!");
                    return;
                }
                default -> System.out.println("Invalid option. Please enter 0-8 or a valid command.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println(" [1] View Inventory     [2] Add Product       [3] Restock Product");
        System.out.println(" [4] Change Price       [5] Sell / Checkout   [6] Sales History");
        System.out.println(" [7] Business Reports   [8] OOP Concepts Demo [0] Exit");
        System.out.println("--------------------------------------------------------------------");
    }

    // =========================================================================
    // 1. VIEW INVENTORY
    // Demonstrates Runtime Polymorphism: calling p.getType() and p.getDetails()
    // on superclass references dynamically invokes Book or Stationery implementations.
    // =========================================================================
    private static void showInventory() {
        List<Product> items = store.getInventory();
        System.out.println("\n=========================== CURRENT INVENTORY ===========================");
        System.out.printf("%-8s %-12s %-26s %-12s %-8s%n", "ID", "TYPE", "TITLE", "PRICE (BDT)", "STOCK");
        System.out.println("-------------------------------------------------------------------------");
        if (items.isEmpty()) {
            System.out.println("No products found in inventory.");
        } else {
            for (Product p : items) {
                // [SUPERCLASS REFERENCE & RUNTIME POLYMORPHISM]:
                // 'p' is typed as Product, but p.getType() dispatches dynamically
                System.out.printf("%-8s %-12s %-26s %-12.2f %-8d%n",
                        p.getId(), p.getType(), cut(p.getTitle(), 24), p.getPrice(), p.getStock());
                System.out.println("   --> Details: " + p.getDetails() + " | Tag: " + p.getCategory());
            }
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    // =========================================================================
    // 2. ADD PRODUCT
    // Demonstrates:
    // - Subclass Reference: Product.Book book = new Product.Book(...)
    // - Accessing subclass-specific methods: book.getAuthor(), stat.getBrand()
    // - Superclass Reference: Product p = book (Upcasting & Polymorphism)
    // =========================================================================
    private static void addProduct() {
        System.out.println("\n--- ADD NEW PRODUCT ---");
        System.out.print("Product Type [B for Book, S for Stationery]: ");
        String type = scanner.nextLine().trim();

        if (!type.equalsIgnoreCase("B") && !type.equalsIgnoreCase("S")) {
            System.out.println("Error: Enter 'B' for Book or 'S' for Stationery.");
            return;
        }

        System.out.print("Enter Unique ID (e.g. B003): ");
        String id = scanner.nextLine().trim();
        if (store.findProduct(id) != null) {
            System.out.println("Error: A product with ID '" + id + "' already exists!");
            return;
        }

        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();

        double price = readDouble("Enter Unit Price (BDT): ");
        int stock = readInt("Enter Initial Stock: ");

        // [SUPERCLASS REFERENCE]: Declared as base class Product
        Product product;

        if (type.equalsIgnoreCase("B")) {
            System.out.print("Enter Author Name: ");
            String author = scanner.nextLine().trim();
            System.out.print("Enter ISBN: ");
            String isbn = scanner.nextLine().trim();

            // [SUBCLASS REFERENCE]: Reference variable of subclass type 'Product.Book'
            // Subclass reference allows direct access to Book-specific methods
            Product.Book book = new Product.Book(id, title, price, stock, author, isbn);
            System.out.println("[Subclass Reference Verified]: Author = " + book.getAuthor() + ", ISBN = " + book.getIsbn());

            // [SUPERCLASS REFERENCE]: Upcasting subclass reference to superclass type
            product = book;
        } else {
            System.out.print("Enter Brand Name: ");
            String brand = scanner.nextLine().trim();

            // [SUBCLASS REFERENCE]: Reference variable of subclass type 'Product.Stationery'
            Product.Stationery stationery = new Product.Stationery(id, title, price, stock, brand);
            System.out.println("[Subclass Reference Verified]: Brand = " + stationery.getBrand());

            // [SUPERCLASS REFERENCE]: Upcasting subclass reference to superclass type
            product = stationery;
        }

        store.addProduct(product);
        System.out.println("Success: Product added and saved to inventory!");
    }

    // =========================================================================
    // 3. RESTOCK PRODUCT
    // Demonstrates Method Overloading: calls store.restock(Product, int)
    // =========================================================================
    private static void restockProduct() {
        System.out.println("\n--- RESTOCK PRODUCT ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();
        Product p = store.findProduct(id);
        if (p == null) {
            System.out.println("Error: Product not found with ID: " + id);
            return;
        }

        System.out.println("Current Product: " + p.getTitle() + " | Stock: " + p.getStock());
        int qty = readInt("Enter Quantity to Add: ");
        try {
            // [METHOD OVERLOADING]: Using overloaded restock(Product, int) method
            store.restock(p, qty);
            System.out.println("Success! New stock for " + p.getId() + " is " + p.getStock() + " units.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. CHANGE PRICE
    // =========================================================================
    private static void changePrice() {
        System.out.println("\n--- UPDATE PRODUCT PRICE ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();
        Product p = store.findProduct(id);
        if (p == null) {
            System.out.println("Error: Product not found with ID: " + id);
            return;
        }

        System.out.println("Current Product: " + p.getTitle() + " | Current Price: BDT " + p.getPrice());
        double newPrice = readDouble("Enter New Unit Price (BDT): ");
        try {
            store.changePrice(id, newPrice);
            System.out.println("Success! Price updated to BDT " + newPrice);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. CHECKOUT / SELL
    // Handles checkout, discounts, payment, and change calculation
    // =========================================================================
    private static void checkout() {
        System.out.println("\n========================= CHECKOUT / SALE =========================");
        System.out.print("Enter Product ID to sell: ");
        String id = scanner.nextLine().trim();
        Product p = store.findProduct(id);
        if (p == null) {
            System.out.println("Error: Product not found: " + id);
            return;
        }

        System.out.println("Selected: " + p.getTitle() + " | Price: BDT " + p.getPrice() + " | Available Stock: " + p.getStock());
        if (p.getStock() <= 0) {
            System.out.println("Error: This product is currently OUT OF STOCK.");
            return;
        }

        int qty = readInt("Enter Quantity to Buy: ");
        if (qty > p.getStock()) {
            System.out.println("Error: Not enough stock. Only " + p.getStock() + " available.");
            return;
        }

        System.out.print("Apply 5% Member Discount? (y/n): ");
        boolean member = scanner.nextLine().trim().equalsIgnoreCase("y");

        double subtotal = p.getPrice() * qty;
        double discount = member ? (subtotal * 0.05) : 0.0;
        double total = subtotal - discount;

        System.out.printf("Subtotal: BDT %.2f | Discount: BDT %.2f | TOTAL DUE: BDT %.2f%n", subtotal, discount, total);

        double cashPaid = readDouble("Enter Cash Received (BDT): ");
        if (cashPaid < total) {
            System.out.printf("Error: Payment insufficient. Need at least BDT %.2f%n", total);
            return;
        }

        try {
            Bookstore.Sale receipt = store.sell(id, qty, member, cashPaid);
            System.out.println("\n----------------- SALES RECEIPT #" + receipt.id() + " -----------------");
            System.out.println("Product: " + receipt.title() + " (ID: " + receipt.productId() + ")");
            System.out.println("Quantity: " + receipt.quantity() + " x BDT " + receipt.unitPrice());
            System.out.printf("Discount: BDT %.2f%n", receipt.discount());
            System.out.printf("TOTAL: BDT %.2f%n", receipt.total());
            System.out.printf("Cash Paid: BDT %.2f%n", receipt.paid());
            System.out.printf("CHANGE RETURNED: BDT %.2f%n", receipt.change());
            System.out.println("---------------------------------------------------------");
            System.out.println("Sale completed successfully!");
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // 6. SALES HISTORY
    // =========================================================================
    private static void showSalesHistory() {
        List<Bookstore.Sale> sales = store.getSales();
        System.out.println("\n============================= SALES HISTORY =============================");
        System.out.printf("%-6s %-8s %-24s %-6s %-12s %-10s%n", "REC#", "PROD ID", "TITLE", "QTY", "TOTAL (BDT)", "CHANGE");
        System.out.println("-------------------------------------------------------------------------");
        if (sales.isEmpty()) {
            System.out.println("No sales transactions recorded yet.");
        } else {
            for (Bookstore.Sale s : sales) {
                System.out.printf("%-6d %-8s %-24s %-6d %-12.2f %-10.2f%n",
                        s.id(), s.productId(), cut(s.title(), 22), s.quantity(), s.total(), s.change());
            }
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    // =========================================================================
    // 7. BUSINESS REPORTS
    // =========================================================================
    private static void showReports() {
        List<Product> products = store.getInventory();
        List<Bookstore.Sale> sales = store.getSales();

        double totalRevenue = 0.0;
        int totalUnitsSold = 0;
        for (Bookstore.Sale s : sales) {
            totalRevenue += s.total();
            totalUnitsSold += s.quantity();
        }

        double inventoryValue = 0.0;
        int totalUnitsInStock = 0;
        for (Product p : products) {
            inventoryValue += (p.getPrice() * p.getStock());
            totalUnitsInStock += p.getStock();
        }

        System.out.println("\n======================== BUSINESS SUMMARY REPORT ========================");
        System.out.printf(" Total Unique Products in Catalog : %d%n", products.size());
        System.out.printf(" Total Units in Stock             : %d units%n", totalUnitsInStock);
        System.out.printf(" Total Inventory Asset Value      : BDT %.2f%n", inventoryValue);
        System.out.printf(" Total Receipts Issued            : %d%n", sales.size());
        System.out.printf(" Total Units Sold                 : %d units%n", totalUnitsSold);
        System.out.printf(" Total Revenue Earned             : BDT %.2f%n", totalRevenue);
        System.out.println("=========================================================================");
    }

    // =========================================================================
    // 8. DEMONSTRATE ALL OOP CONCEPTS
    // Direct code verification of all required OOP features.
    // =========================================================================
    private static void demonstrateOOP() {
        System.out.println("\n====================================================================");
        System.out.println("            OBJECT ORIENTED PROGRAMMING (OOP) DEMONSTRATION         ");
        System.out.println("====================================================================");

        // 1. Static vs Non-Static
        System.out.println("1. [STATIC & NON-STATIC]:");
        System.out.println("   - Static Variable: Product.CURRENCY = " + Product.CURRENCY);
        System.out.println("   - Static Method: Product.getTotalProductsCount() = " + Product.getTotalProductsCount());

        // 2. Subclass Reference vs Superclass Reference
        System.out.println("\n2. [SUBCLASS REFERENCE vs SUPERCLASS REFERENCE]:");
        // Subclass reference
        Product.Book myBook = new Product.Book("DEMO1", "OOP in Java", 400.0, 5, "Dr. James", "111-222-333");
        System.out.println("   - Subclass Reference (Product.Book): can access author -> " + myBook.getAuthor());

        // Superclass reference (Upcasting)
        Product superRef = myBook;
        System.out.println("   - Superclass Reference (Product): dynamically calls getDetails() ->");
        System.out.println("     " + superRef.getDetails()); // Dynamic dispatch / Runtime Polymorphism

        // 3. Overloading vs Overriding
        System.out.println("\n3. [OVERLOADING & OVERRIDING (POLYMORPHISM)]:");
        System.out.println("   - Overloading 1 (10% discount): BDT " + superRef.calculateDiscount(10.0));
        System.out.println("   - Overloading 2 (10% with 25 BDT cap): BDT " + superRef.calculateDiscount(10.0, 25.0));
        System.out.println("   - Overriding: Book overrides getType() returning -> " + superRef.getType());

        // 4. Diamond Problem Resolution
        System.out.println("\n4. [DIAMOND PROBLEM RESOLUTION]:");
        System.out.println("   Trackable and Promotable both inherit from Item and provide default getCategory().");
        System.out.println("   Product resolves the diamond conflict via Trackable.super & Promotable.super:");
        System.out.println("   --> Product.getCategory() = " + superRef.getCategory());

        // 5. Encapsulation & this / super
        System.out.println("\n5. [ENCAPSULATION, 'this' & 'super']:");
        System.out.println("   - Encapsulation: Fields are private; accessed via getters (Price = " + superRef.getPrice() + ")");
        System.out.println("   - 'this' keyword: Resolves variable shadowing and supports constructor chaining.");
        System.out.println("   - 'super' keyword: Calls superclass constructor to initialize base properties.");
        System.out.println("====================================================================");
    }

    // Helper utilities for clean, safe terminal input
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Please try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static String cut(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
