import java.util.Locale;

// Starts the program and runs the menu option the user picks.
public class Main {
    static BookstoreUI ui = new BookstoreUI();
    static Bookstore store;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // always show prices as 450.50 (with a dot) on every computer
        try {
            store = Bookstore.load();
        } catch (RuntimeException e) {
            ui.error(e.getMessage());
            System.exit(1); // stop with an error code, so run.bat keeps the window open to read this
        }
        ui.newScreen(1);
        ui.showProducts(store.getProducts());
        while (true) {
            try {
                String choice = ui.askMenuChoice().toLowerCase();
                switch (choice) {
                    case "1": ui.newScreen(1); ui.showProducts(store.getProducts()); break;
                    case "2": ui.newScreen(2); search(); break;
                    case "3": ui.newScreen(3); addProduct(); break;
                    case "4": ui.newScreen(4); restock(); break;
                    case "5": ui.newScreen(5); changePrice(); break;
                    case "6": ui.newScreen(6); sell(); break;
                    case "7": ui.newScreen(7); refund(); break;
                    case "8": ui.newScreen(8); ui.showSales(store.getSales()); break;
                    case "9": ui.newScreen(9); ui.showReport(store); break;
                    case "oop": oopDemo(); break;
                    case "0": ui.goodbye(); return;
                    default: ui.error("Please choose a number from the menu.");
                }
            } catch (RuntimeException e) {
                ui.error(e.getMessage()); // show the problem in red, then go back to the menu
            }
        }
    }

    // Asks for a product ID until it matches a product (or the user types cancel).
    static Product askProduct() {
        while (true) {
            Product p = store.find(ui.askText("Product ID"));
            if (p != null) return p;
            ui.error("No product has that ID. Check the table above and try again.");
        }
    }

    // After a task: draw the screen again with the updated table and a green message.
    static void showResult(int tab, String text) {
        ui.newScreen(tab);
        ui.showProducts(store.getProducts());
        ui.message(text);
    }

    static void search() {
        String text = ui.askText("Search for (ID, title, author, brand or ISBN)");
        ui.newScreen(2);
        ui.showProducts(store.search(text));
        ui.message("Results for \"" + text + "\"");
    }

    static void addProduct() {
        char type = ui.askLetter("Type: B = book, S = stationery", "BS");
        String id = ui.askText("ID (short code, e.g. B1)");
        while (store.find(id) != null) {
            ui.error("ID " + id + " is already used. Please choose another.");
            id = ui.askText("ID (short code, e.g. B1)");
        }
        String title = ui.askText("Title");
        double price = ui.askMoney("Price (BDT)", 0);
        int stock = ui.askNumber("Stock", 0, 1000000);
        Product product; // SUPERCLASS reference: can hold a Book or a Stationery
        String extra;
        if (type == 'B') {
            Book book = new Book(id, title, price, stock, ui.askText("Author"), ui.askText("ISBN"));
            extra = " by " + book.getAuthor(); // SUBCLASS reference: getAuthor() works only here
            product = book;                    // UPCASTING: the Book is stored as a Product
        } else {
            Stationery item = new Stationery(id, title, price, stock, ui.askText("Brand"));
            extra = " from " + item.getBrand();
            product = item;
        }
        store.add(product);
        showResult(3, "Added " + product.getType() + " \"" + title + "\"" + extra + ".");
    }

    static void restock() {
        ui.showProducts(store.getProducts());
        Product p = askProduct();
        ui.showProduct(p);
        store.restock(p.getId(), ui.askNumber("Quantity to add", 1, 1000000)); // overloaded restock(String, int)
        showResult(4, "Restocked \"" + p.getTitle() + "\". Stock is now " + p.getStock() + ".");
    }

    static void changePrice() {
        ui.showProducts(store.getProducts());
        Product p = askProduct();
        ui.showProduct(p);
        store.changePrice(p, ui.askMoney("New price (BDT)", 0));
        showResult(5, String.format("Price of \"%s\" is now %.2f BDT.", p.getTitle(), p.getPrice()));
    }

    static void sell() {
        ui.showProducts(store.getProducts());
        Product p = askProduct();
        while (p.getStock() == 0) {
            ui.error("\"" + p.getTitle() + "\" is out of stock. Choose another product.");
            p = askProduct();
        }
        ui.showProduct(p);
        int quantity = ui.askNumber("Quantity (1-" + p.getStock() + ")", 1, p.getStock());
        boolean member = ui.askYes("Member? 5% off");
        double total = store.total(p, quantity, member);
        ui.message(String.format("Total: %.2f BDT", total));
        Sale sale = store.sell(p, quantity, member, ui.askMoney("Cash received (BDT)", total));
        ui.newScreen(6);
        ui.showReceipt(sale);
        ui.message(String.format("Sale saved. Give back %.2f BDT change.", sale.paid - sale.total));
    }

    static void refund() {
        if (store.getSales().isEmpty()) throw new RuntimeException("No sales yet, so there is nothing to refund.");
        ui.showSales(store.getSales());
        Sale sale = store.getSale(ui.askNumber("Receipt number", 1, store.getSales().size()));
        if (sale.refunded) throw new RuntimeException("Receipt #" + sale.id + " was already refunded.");
        ui.showReceipt(sale);
        if (!ui.askYes("Refund this sale")) {
            ui.message("Not refunded. Nothing changed.");
            return;
        }
        store.refund(sale);
        ui.newScreen(7);
        ui.showReceipt(sale);
        ui.message(String.format("Refunded. Give back %.2f BDT. Stock returned.", sale.total));
    }

    // Shows every OOP concept working, using two demo objects (they are not saved).
    static void oopDemo() {
        ui.newScreen(-1, "OOP CONCEPTS DEMO"); // overloaded newScreen(int, String)
        Book book = new Book("D1", "Java Basics", 100, 5, "Ann", "111"); // SUBCLASS reference
        Stationery pen = new Stationery("D2", "Gel Pen", 20, "Pilot");    // overloaded constructor (no stock)
        Product p = book;                                                 // SUPERCLASS reference (upcasting)
        book.setPrice(-50);                                               // the setter refuses this

        ui.showConcept("1 Abstraction", "Product is abstract: we make a Book or a Stationery");
        ui.showConcept("2 Encapsulation", String.format("price is private; setPrice(-50) refused: %.2f", book.getPrice()));
        ui.showConcept("3 Inheritance", "Book extends Product, so book.getTitle() = " + book.getTitle());
        Product[] items = {book, pen};
        for (Product item : items) {
            ui.showConcept("4 Polymorphism", "item.getDetails() -> " + item.getDetails()); // same call, two results
        }
        ui.showConcept("5 Static", "Product.CURRENCY = " + Product.CURRENCY + ", Product.getCreatedCount() = " + Product.getCreatedCount());
        ui.showConcept("6 Overloading", String.format("calculateDiscount(10) = %.2f, (10, 5) = %.2f",
                book.calculateDiscount(10), book.calculateDiscount(10, 5)));
        ui.showConcept("7 Overriding", "book.getType() = " + book.getType() + ", pen.getType() = " + pen.getType());
        ui.showConcept("8 this", "this(id, title, price, 0) gave the pen stock = " + pen.getStock());
        ui.showConcept("9 super", "Book's constructor starts with super(id, title, price, stock)");
        ui.showConcept("10 Sub ref", "Book book -> book.getAuthor() = " + book.getAuthor());
        ui.showConcept("10 Super ref", "Product p = book -> p.getType() = " + p.getType() + " (no getAuthor)");
        ui.showConcept("11 Diamond", "p.getCategory() = " + p.getCategory());
        ui.message("Demo objects are not saved. See README.md for where each concept is.");
    }
}
