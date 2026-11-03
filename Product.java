import java.io.Serializable;

// DIAMOND PROBLEM: Trackable and Promotable both extend Item, and both give their own
// default getCategory(). Product implements both, so Java forces Product to choose.
//
//                Item
//              /      \
//       Trackable    Promotable
//              \      /
//              Product
interface Item {
    default String getCategory() { return "Item"; }
}

interface Trackable extends Item {
    @Override
    default String getCategory() { return "Tracked stock"; }
}

interface Promotable extends Item {
    @Override
    default String getCategory() { return "Promotable"; }
}

// ABSTRACTION: Product is abstract, so the program can only make a Book or a Stationery.
// ENCAPSULATION: the fields are private and are used only through getters and setters.
public abstract class Product implements Trackable, Promotable, Serializable {
    private static final long serialVersionUID = 1L; // keeps saved files readable after code edits

    // STATIC variables belong to the class and are shared by every product.
    public static final String CURRENCY;
    private static int createdCount = 0;

    // STATIC BLOCK: runs once, when Java first loads the Product class.
    static {
        CURRENCY = "BDT";
    }

    // NON-STATIC (instance) variables: every product object has its own copy.
    private String id, title;
    private double price;
    private int stock;

    // CONSTRUCTOR OVERLOADING 1: all four values.
    public Product(String id, String title, double price, int stock) {
        this.id = id; // THIS: this.id is the object's field, id is the parameter
        this.title = title;
        this.price = price;
        this.stock = stock;
        createdCount++;
    }

    // CONSTRUCTOR OVERLOADING 2: no stock given. THIS(...) calls the constructor above.
    public Product(String id, String title, double price) {
        this(id, title, price, 0);
    }

    // STATIC METHOD: called on the class itself, e.g. Product.getCreatedCount()
    public static int getCreatedCount() { return createdCount; }

    // NON-STATIC METHODS: getters and setters work on one object's own data.
    public String getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    // The setters protect the data: a negative value is refused.
    public void setPrice(double price) {
        if (price >= 0) this.price = price;
    }

    public void setStock(int stock) {
        if (stock >= 0) this.stock = stock;
    }

    // METHOD OVERLOADING 1: discount on one item by percentage.
    public double calculateDiscount(double percentage) {
        return price * percentage / 100;
    }

    // METHOD OVERLOADING 2: same name, one more parameter: discount with a maximum limit.
    public double calculateDiscount(double percentage, double maxDiscount) {
        return Math.min(calculateDiscount(percentage), maxDiscount);
    }

    // DIAMOND PROBLEM SOLVED: we say exactly which parent versions to use.
    @Override
    public String getCategory() {
        return Trackable.super.getCategory() + " & " + Promotable.super.getCategory();
    }

    // ABSTRACT METHODS: every subclass must write its own version (overriding).
    public abstract String getType();
    public abstract String getDetails();
}

// INHERITANCE: Book is a subclass of the superclass Product.
class Book extends Product {
    private static final long serialVersionUID = 1L;
    private String author, isbn;

    public Book(String id, String title, double price, int stock, String author, String isbn) {
        super(id, title, price, stock); // SUPER: runs the Product constructor first
        this.author = author;
        this.isbn = isbn;
    }

    public String getAuthor() { return author; } // only a Book has an author

    // METHOD OVERRIDING: Book's own versions of Product's abstract methods.
    @Override
    public String getType() { return "BOOK"; }

    @Override
    public String getDetails() { return "Book by " + author + ", ISBN " + isbn; }
}

// INHERITANCE: Stationery is another subclass of Product.
class Stationery extends Product {
    private static final long serialVersionUID = 1L;
    private String brand;

    public Stationery(String id, String title, double price, int stock, String brand) {
        super(id, title, price, stock); // SUPER: Product's 4-value constructor
        this.brand = brand;
    }

    // CONSTRUCTOR OVERLOADING: no stock yet, so use Product's 3-value constructor.
    public Stationery(String id, String title, double price, String brand) {
        super(id, title, price);
        this.brand = brand;
    }

    public String getBrand() { return brand; } // only Stationery has a brand

    @Override
    public String getType() { return "STATIONERY"; }

    @Override
    public String getDetails() { return "Stationery by " + brand; }
}
