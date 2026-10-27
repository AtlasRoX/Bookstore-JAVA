import java.util.Locale;

// =============================================================================
// [DIAMOND PROBLEM DEMONSTRATION WITH INTERFACES]
// In Java, multiple class inheritance (class C extends A, B) is forbidden
// to avoid the Diamond Problem (ambiguity from multiple parent classes).
//
// However, Java allows multiple interface inheritance. When two interfaces
// provide conflicting default methods, an interface-level diamond problem occurs:
//
//               Item (default getCategory())
//                /  \
//       Trackable    Promotable  (both override getCategory() with default implementations)
//                \  /
//               Product (COMPILER FORCES Product to override and resolve the conflict!)
// =============================================================================

interface Item {
    default String getCategory() {
        return "General Item";
    }
}

interface Trackable extends Item {
    @Override
    default String getCategory() {
        return "Trackable Inventory";
    }
}

interface Promotable extends Item {
    @Override
    default String getCategory() {
        return "Promotional Merchandise";
    }
}

// =============================================================================
// [ABSTRACTION]: Abstract class Product cannot be instantiated directly.
// It serves as a blueprint for all products in the bookstore.
// It also implements both Trackable and Promotable interfaces.
// =============================================================================
public abstract class Product implements Trackable, Promotable {

    // [STATIC VARIABLE]: Shared across all instances of Product
    public static final String CURRENCY = "BDT";
    private static int totalProductsCount = 0;

    // [STATIC BLOCK]: Executes once when class is loaded into memory
    static {
        // Static class-level initialization
    }

    // [ENCAPSULATION & NON-STATIC (INSTANCE) VARIABLES]:
    // Private variables prevent direct outside modification.
    // Each Product object has its own copy of these fields.
    private String id;
    private String title;
    private double price;
    private int stock;

    // =========================================================================
    // [CONSTRUCTOR OVERLOADING 1]: Full 4-parameter constructor
    // =========================================================================
    public Product(String id, String title, double price, int stock) {
        // [THIS KEYWORD - Shadowing]: 'this.id' refers to instance field, 'id' is parameter
        this.id = (id == null) ? "UNKNOWN" : id.trim().toUpperCase(Locale.ROOT);
        this.title = (title == null) ? "Untitled" : title.trim();
        this.price = Math.max(0.0, price);
        this.stock = Math.max(0, stock);

        // Modifying static counter whenever any product is instantiated
        totalProductsCount++;
    }

    // =========================================================================
    // [CONSTRUCTOR OVERLOADING 2 & THIS KEYWORD - Constructor Chaining]:
    // Calls the 4-parameter constructor using this(...) defaulting stock to 0
    // =========================================================================
    public Product(String id, String title, double price) {
        this(id, title, price, 0); // Constructor chaining
    }

    // =========================================================================
    // [DIAMOND PROBLEM RESOLUTION]:
    // Both Trackable and Promotable define getCategory().
    // Java forces Product to override getCategory() to eliminate ambiguity.
    // We explicitly resolve it using InterfaceName.super.methodName().
    // =========================================================================
    @Override
    public String getCategory() {
        return Trackable.super.getCategory() + " & " + Promotable.super.getCategory();
    }

    // =========================================================================
    // [STATIC METHOD]: Can be called without creating an object instance
    // e.g. Product.getTotalProductsCount()
    // =========================================================================
    public static int getTotalProductsCount() {
        return totalProductsCount;
    }

    // =========================================================================
    // [NON-STATIC METHODS & ENCAPSULATION (GETTERS & SETTERS)]
    // Operate on the specific instance's data.
    // =========================================================================
    public String getId() { return this.id; }
    public String getTitle() { return this.title; }
    public double getPrice() { return this.price; }
    public int getStock() { return this.stock; }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    // =========================================================================
    // [METHOD OVERLOADING 1]: Calculate discount with rate only
    // =========================================================================
    public double calculateDiscount(double percentage) {
        return (this.price * percentage) / 100.0;
    }

    // =========================================================================
    // [METHOD OVERLOADING 2]: Calculate discount with rate AND max limit
    // =========================================================================
    public double calculateDiscount(double percentage, double maxCap) {
        double discount = calculateDiscount(percentage);
        return Math.min(discount, maxCap);
    }

    // =========================================================================
    // [ABSTRACTION]: Abstract methods that concrete subclasses MUST implement
    // =========================================================================
    public abstract String getType();
    public abstract String getDetails();

    // =========================================================================
    // [INHERITANCE]: Book IS-A Product (subclass inherits from superclass Product)
    // =========================================================================
    public static class Book extends Product {
        // [ENCAPSULATION]: Subclass-specific private attributes
        private String author;
        private String isbn;

        // [CONSTRUCTOR OVERLOADING 1]
        public Book(String id, String title, double price, int stock, String author, String isbn) {
            // [SUPER KEYWORD]: Calls the superclass (Product) constructor
            super(id, title, price, stock);
            // [THIS KEYWORD]
            this.author = (author == null) ? "Unknown" : author.trim();
            this.isbn = (isbn == null) ? "N/A" : isbn.trim();
        }

        // [CONSTRUCTOR OVERLOADING 2 & THIS KEYWORD: Constructor Chaining]
        public Book(String id, String title, double price, String author, String isbn) {
            this(id, title, price, 0, author, isbn);
        }

        // [SUBCLASS-SPECIFIC GETTERS]: Accessible via Subclass Reference
        public String getAuthor() { return this.author; }
        public String getIsbn() { return this.isbn; }

        // =====================================================================
        // [METHOD OVERRIDING & RUNTIME POLYMORPHISM]:
        // Subclass provides its own concrete implementation of abstract methods
        // =====================================================================
        @Override
        public String getType() {
            return "BOOK";
        }

        @Override
        public String getDetails() {
            return "Author: " + this.author + " | ISBN: " + this.isbn;
        }
    }

    // =========================================================================
    // [INHERITANCE]: Stationery IS-A Product (subclass inherits from Product)
    // =========================================================================
    public static class Stationery extends Product {
        // [ENCAPSULATION]: Subclass-specific private attribute
        private String brand;

        // [CONSTRUCTOR OVERLOADING 1]
        public Stationery(String id, String title, double price, int stock, String brand) {
            // [SUPER KEYWORD]: Calls the superclass constructor
            super(id, title, price, stock);
            // [THIS KEYWORD]
            this.brand = (brand == null) ? "Generic" : brand.trim();
        }

        // [CONSTRUCTOR OVERLOADING 2 & THIS KEYWORD: Constructor Chaining]
        public Stationery(String id, String title, double price, String brand) {
            this(id, title, price, 0, brand);
        }

        // [SUBCLASS-SPECIFIC GETTER]: Accessible via Subclass Reference
        public String getBrand() { return this.brand; }

        // =====================================================================
        // [METHOD OVERRIDING & RUNTIME POLYMORPHISM]
        // =====================================================================
        @Override
        public String getType() {
            return "STATIONERY";
        }

        @Override
        public String getDetails() {
            return "Brand: " + this.brand;
        }
    }
}
