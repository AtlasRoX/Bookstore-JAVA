import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

// The bookstore rules and the data file. This class never prints anything.
public class Bookstore implements Serializable {
    private static final long serialVersionUID = 1L; // keeps saved files readable after code edits
    private static final String FILE = "bookstore.dat"; // where everything is saved
    private ArrayList<Product> products = new ArrayList<>(); // all products
    private ArrayList<Sale> sales = new ArrayList<>(); // all receipts

    // Getters for the two lists.
    public ArrayList<Product> getProducts() { return products; }
    public ArrayList<Sale> getSales() { return sales; }

    // Returns the product with this ID, or null if there is none.
    public Product find(String id) {
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    // Same as find, but stops with an error message if the ID does not exist.
    public Product get(String id) {
        Product p = find(id);
        if (p == null) throw new RuntimeException("No product with ID " + id + ".");
        return p;
    }

    // Products whose ID, title or details contain the text (any case).
    public ArrayList<Product> search(String text) {
        ArrayList<Product> found = new ArrayList<>();
        for (Product p : products) {
            String all = p.getId() + " " + p.getTitle() + " " + p.getDetails();
            if (all.toLowerCase().contains(text.toLowerCase())) found.add(p);
        }
        return found;
    }

    // Adds a new product (its ID must be new) and saves.
    public void add(Product p) {
        if (find(p.getId()) != null) throw new RuntimeException("ID " + p.getId() + " is already used.");
        products.add(p);
        save();
    }

    // METHOD OVERLOADING: restock by product ID, or by the product object itself.
    public void restock(String id, int quantity) {
        restock(get(id), quantity);
    }

    // Adds units to the stock and saves.
    public void restock(Product p, int quantity) {
        if (quantity < 1) throw new RuntimeException("Quantity must be at least 1.");
        p.setStock(p.getStock() + quantity);
        save();
    }

    // Sets a new unit price and saves.
    public void changePrice(Product p, double price) {
        p.setPrice(price);
        save();
    }

    // Members get 5% off. The total is rounded to 2 decimal places.
    public double total(Product p, int quantity, boolean member) {
        if (quantity < 1 || quantity > p.getStock())
            throw new RuntimeException("Quantity must be from 1 to " + p.getStock() + ".");
        double total = p.getPrice() * quantity;
        if (member) total = total - p.calculateDiscount(5) * quantity;
        return Math.round(total * 100) / 100.0;
    }

    // Sells: checks the cash, lowers the stock, stores the receipt and saves.
    public Sale sell(Product p, int quantity, boolean member, double paid) {
        double total = total(p, quantity, member);
        if (paid < total) throw new RuntimeException("Cash is less than the total. Sale cancelled.");
        p.setStock(p.getStock() - quantity);
        Sale sale = new Sale(sales.size() + 1, p, quantity, total, paid);
        sales.add(sale);
        save();
        return sale;
    }

    // Returns receipt number N (receipts start at 1).
    public Sale getSale(int number) {
        if (number < 1 || number > sales.size()) throw new RuntimeException("No receipt #" + number + ".");
        return sales.get(number - 1);
    }

    // Marks the receipt refunded and puts the units back in stock.
    public void refund(Sale sale) {
        if (sale.refunded) throw new RuntimeException("Receipt #" + sale.id + " was already refunded.");
        sale.refunded = true;
        Product p = get(sale.productId);
        p.setStock(p.getStock() + sale.quantity);
        save();
    }

    // Saves the whole bookstore (products and sales) to the file in one step.
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Could not save: " + e.getMessage());
        }
    }

    // Reads the saved bookstore, or starts an empty one the first time.
    public static Bookstore load() {
        if (!new File(FILE).exists()) return new Bookstore();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {
            return (Bookstore) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Could not read " + FILE + ": " + e.getMessage());
        }
    }
}

// One sale (receipt). It keeps the title and total even if the product changes later.
class Sale implements Serializable {
    private static final long serialVersionUID = 1L;
    int id, quantity;
    String date, productId, title;
    double total, paid;
    boolean refunded;

    // Copies the product's ID and title, so an old receipt never changes.
    Sale(int id, Product p, int quantity, double total, double paid) {
        this.id = id;
        this.date = LocalDate.now().toString();
        this.productId = p.getId();
        this.title = p.getTitle();
        this.quantity = quantity;
        this.total = total;
        this.paid = paid;
    }
}
