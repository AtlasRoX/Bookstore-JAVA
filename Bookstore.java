import java.io.*;
import java.util.*;

/**
 * Bookstore manages inventory catalog, sales records, and persistence.
 * Demonstrates Encapsulation, Superclass Reference collections, and Method Overloading.
 */
public class Bookstore {

    // Simple record for storing sales receipts
    public record Sale(int id, String productId, String title, int quantity,
                       double unitPrice, double discount, double total, double paid, double change) {}

    // [ENCAPSULATION & NON-STATIC VARIABLES]: Private internal state
    // [SUPERCLASS REFERENCE in Collection]:
    // 'inventory' holds Product superclass references pointing to Book or Stationery objects.
    private final List<Product> inventory = new ArrayList<>();
    private final List<Sale> sales = new ArrayList<>();
    private final File dataFile;
    private int nextSaleId = 1;

    public Bookstore(String filename) {
        // [THIS KEYWORD]
        this.dataFile = new File(filename);
        load();
    }

    public List<Product> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public List<Sale> getSales() {
        return Collections.unmodifiableList(sales);
    }

    // [SUPERCLASS REFERENCE PARAMETER]: Accepts any Product subclass
    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null.");
        if (findProduct(product.getId()) != null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " already exists.");
        }
        inventory.add(product);
        save();
    }

    // =========================================================================
    // [METHOD OVERLOADING 1]: Find product by ID string
    // Returns a Superclass Reference (Product)
    // =========================================================================
    public Product findProduct(String id) {
        if (id == null) return null;
        for (Product p : inventory) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    // =========================================================================
    // [METHOD OVERLOADING 2]: Find product by existing Product reference
    // =========================================================================
    public Product findProduct(Product product) {
        if (product == null) return null;
        return findProduct(product.getId());
    }

    // =========================================================================
    // [METHOD OVERLOADING 1]: Restock by Product ID string
    // =========================================================================
    public void restock(String id, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Restock quantity must be positive.");
        Product p = findProduct(id);
        if (p == null) throw new IllegalArgumentException("Product not found: " + id);
        p.setStock(p.getStock() + quantity);
        save();
    }

    // =========================================================================
    // [METHOD OVERLOADING 2]: Restock by Product reference
    // =========================================================================
    public void restock(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null.");
        restock(product.getId(), quantity); // Delegating to overloaded method
    }

    public void changePrice(String id, double newPrice) {
        if (newPrice < 0) throw new IllegalArgumentException("Price cannot be negative.");
        Product p = findProduct(id);
        if (p == null) throw new IllegalArgumentException("Product not found: " + id);
        p.setPrice(newPrice);
        save();
    }

    public Sale sell(String id, int quantity, boolean memberDiscount, double cashPaid) {
        Product p = findProduct(id);
        if (p == null) throw new IllegalArgumentException("Product not found: " + id);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        if (quantity > p.getStock()) throw new IllegalArgumentException("Insufficient stock. Available: " + p.getStock());

        double subtotal = p.getPrice() * quantity;
        double discountRate = memberDiscount ? 5.0 : 0.0;
        double discount = (subtotal * discountRate) / 100.0;
        double total = subtotal - discount;

        if (cashPaid < total) {
            throw new IllegalArgumentException(String.format("Payment insufficient. Total is BDT %.2f, paid BDT %.2f", total, cashPaid));
        }

        double change = cashPaid - total;

        // Deduct stock
        p.setStock(p.getStock() - quantity);

        Sale sale = new Sale(nextSaleId++, p.getId(), p.getTitle(), quantity, p.getPrice(), discount, total, cashPaid, change);
        sales.add(sale);
        save();
        return sale;
    }

    // =========================================================================
    // Clean and simple file storage (persists inventory and sales)
    // =========================================================================
    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            writer.println("# PRODUCTS");
            for (Product p : inventory) {
                if (p instanceof Product.Book b) {
                    writer.printf("BOOK|%s|%s|%.2f|%d|%s|%s%n", b.getId(), b.getTitle(), b.getPrice(), b.getStock(), b.getAuthor(), b.getIsbn());
                } else if (p instanceof Product.Stationery s) {
                    writer.printf("STAT|%s|%s|%.2f|%d|%s%n", s.getId(), s.getTitle(), s.getPrice(), s.getStock(), s.getBrand());
                }
            }
            writer.println("# SALES");
            for (Sale s : sales) {
                writer.printf("SALE|%d|%s|%s|%d|%.2f|%.2f|%.2f|%.2f|%.2f%n",
                        s.id(), s.productId(), s.title(), s.quantity(), s.unitPrice(), s.discount(), s.total(), s.paid(), s.change());
            }
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
        }
    }

    private void load() {
        if (!dataFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts[0].equals("BOOK") && parts.length >= 7) {
                    inventory.add(new Product.Book(parts[1], parts[2], Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4]), parts[5], parts[6]));
                } else if (parts[0].equals("STAT") && parts.length >= 6) {
                    inventory.add(new Product.Stationery(parts[1], parts[2], Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4]), parts[5]));
                } else if (parts[0].equals("SALE") && parts.length >= 10) {
                    int id = Integer.parseInt(parts[1]);
                    sales.add(new Sale(id, parts[2], parts[3], Integer.parseInt(parts[4]),
                            Double.parseDouble(parts[5]), Double.parseDouble(parts[6]),
                            Double.parseDouble(parts[7]), Double.parseDouble(parts[8]),
                            Double.parseDouble(parts[9])));
                    if (id >= nextSaleId) nextSaleId = id + 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Note: Starting with fresh/default inventory (" + e.getMessage() + ")");
        }
    }
}
