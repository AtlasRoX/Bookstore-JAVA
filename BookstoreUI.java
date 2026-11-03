import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

// Everything the user sees or types goes through this class.
// Texts like "\033[...m" are ANSI color codes that the terminal shows as colors:
// "38;2;R;G;B" sets the text color and "48;2;R;G;B" sets the background color.
public class BookstoreUI {
    private static final String BODY = "\033[38;2;229;237;255;48;2;12;22;66m";     // light text on navy
    private static final String DIM = "\033[38;2;140;189;225;48;2;12;22;66m";      // soft blue on navy
    private static final String YELLOW = "\033[38;2;248;212;123;48;2;12;22;66m";   // yellow on navy
    private static final String CYAN = "\033[38;2;9;25;47;48;2;103;199;211m";      // dark on cyan
    private static final String PURPLE = "\033[38;2;255;241;255;48;2;97;43;123m";  // white on purple
    private static final String WHITE = "\033[38;2;12;22;66;48;2;222;233;255m";    // navy on white
    private static final String GREEN = "\033[38;2;13;43;31;48;2;132;210;164m";    // dark on green
    private static final String RED = "\033[38;2;255;239;239;48;2;137;35;50m";     // white on red
    private static final String RESET = "\033[0m";                                 // terminal's own colors
    private static final String[] MENU = {"Exit", "Products", "Search", "Add product", "Restock",
            "Change price", "Sell", "Refund", "Sales", "Report"};
    private Scanner in = new Scanner(System.in);

    public BookstoreUI() {
        System.out.print("\033]0;Bookstore\007"); // window title
        // Give the terminal its normal colors back when the program ends (even after Ctrl+C).
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.print(RESET)));
    }

    // Prints one line: the text in its color, then the rest of the line in navy.
    private void line(String color, String text) {
        System.out.println(color + text + BODY + "\033[K");
    }

    // Shortens text that is too long for a table column, e.g. "A Very Long Ti..."
    private String fit(String text, int width) {
        if (text.length() <= width) return text;
        return text.substring(0, width - 3) + "...";
    }

    // Clears the screen (and old screens above it), then draws the title bar, menu tabs and screen name.
    public void newScreen(int tab) {
        System.out.print(BODY + "\033[2J\033[3J\033[H");
        line(CYAN, String.format(" %-60s%17s ", "BOOKSTORE / INVENTORY MANAGER", "JAVA / LOCAL"));
        String tabs = "";
        for (int n = 1; n <= 10; n++) {
            int number = n % 10; // 1, 2, ... 9, then 0 for Exit
            String color = (number == tab) ? WHITE : BODY;
            tabs = tabs + color + String.format(" %d %-12s", number, MENU[number]) + BODY;
            if (n == 5 || n == 10) {
                line(BODY, tabs);
                tabs = "";
            }
        }
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        line(PURPLE, String.format(" %-60s%17s ", MENU[tab].toUpperCase(), time));
        line(BODY, "");
    }

    // The bottom of every screen: a hint, then the menu question.
    public String askMenuChoice() {
        line(DIM, " " + "-".repeat(77));
        line(DIM, " Type a menu number and press Enter. In any task, type cancel to stop.");
        return askText("Choose");
    }

    public void message(String text) {
        line(GREEN, String.format(" %-78s", text));
    }

    public void error(String text) {
        line(RED, String.format(" %-78s", text));
    }

    public void goodbye() {
        System.out.print(RESET + "\033[2J\033[H");
        System.out.println("Goodbye! Everything is saved.");
    }

    // Keeps asking until something is typed. Typing "cancel" stops the current task.
    public String askText(String question) {
        while (true) {
            // Draw a purple bar, then \r jumps back so the typing starts right after the question.
            System.out.print(PURPLE + String.format(" %-78s", question + " >") + "\r " + question + " > ");
            if (!in.hasNextLine()) System.exit(0); // input was closed
            String answer = in.nextLine().trim();
            System.out.print(BODY);
            if (answer.equalsIgnoreCase("cancel")) throw new RuntimeException("Cancelled. Nothing changed.");
            if (!answer.isEmpty()) return answer;
        }
    }

    // Asks again until the answer is a whole number from min to max.
    public int askNumber(String question, int min, int max) {
        while (true) {
            try {
                int number = Integer.parseInt(askText(question));
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException e) {
                // not a number, so ask again
            }
            error("Please enter a whole number from " + min + " to " + max + ".");
        }
    }

    // Asks again until the answer is an amount of at least min (rounded to 2 decimals).
    public double askMoney(String question, double min) {
        while (true) {
            try {
                double amount = Math.round(Double.parseDouble(askText(question)) * 100) / 100.0;
                if (amount >= min && amount <= 1000000000) return amount;
            } catch (NumberFormatException e) {
                // not a number, so ask again
            }
            error(min > 0 ? String.format("Please enter at least %.2f BDT.", min) : "Please enter an amount like 250 or 99.50.");
        }
    }

    // Asks again until the answer starts with one of two letters, e.g. "YN" or "BS".
    public char askLetter(String question, String letters) {
        while (true) {
            char letter = Character.toUpperCase(askText(question).charAt(0));
            if (letters.indexOf(letter) >= 0) return letter;
            error("Please type " + letters.charAt(0) + " or " + letters.charAt(1) + ".");
        }
    }

    public boolean askYes(String question) {
        return askLetter(question + " (y/n)", "YN") == 'Y';
    }

    // A table of products. Low stock (3 or fewer) is yellow and marked with "!".
    public void showProducts(ArrayList<Product> list) {
        line(CYAN, String.format(" %-9s %-21s %-29s %9s %6s", "ID", "TITLE", "DETAILS", "PRICE BDT", "STOCK"));
        int low = 0;
        for (Product p : list) {
            String color = BODY;
            String stock = p.getStock() + " ";
            if (p.getStock() <= 3) {
                color = YELLOW;
                stock = p.getStock() + "!";
                low++;
            }
            line(color, String.format(" %-9s %-21s %-29s %9.2f %6s",
                    fit(p.getId(), 9), fit(p.getTitle(), 21), fit(p.getDetails(), 29), p.getPrice(), stock));
        }
        if (list.isEmpty()) line(DIM, " No products found. Choose 3 to add one.");
        line(DIM, " Products shown: " + list.size() + "  |  Low on stock: " + low + " (yellow, marked !)");
        line(BODY, "");
    }

    // One white "card" line, used for the product card and the receipt.
    private void card(String text) {
        line(WHITE, String.format("   %-44s ", fit(text, 44)));
    }

    public void showProduct(Product p) {
        card(p.getType() + ": " + p.getTitle() + "  (" + p.getId() + ")");
        card(p.getDetails());
        card("Category: " + p.getCategory());
        card(String.format("Price %.2f %s   |   In stock: %d", p.getPrice(), Product.CURRENCY, p.getStock()));
        line(BODY, "");
    }

    public void showReceipt(Sale s) {
        card("");
        card("RECEIPT #" + s.id + "     " + s.date + "     " + (s.refunded ? "REFUNDED" : "PAID"));
        card("- - - - - - - - - - - - - - - - - - - - - -");
        card(s.title + "  x " + s.quantity);
        card(String.format("Total            %14.2f BDT", s.total));
        card(String.format("Cash received    %14.2f BDT", s.paid));
        card(String.format("Change           %14.2f BDT", s.paid - s.total));
        card("");
        line(BODY, "");
    }

    // A table of all sales. Refunded sales are yellow.
    public void showSales(ArrayList<Sale> list) {
        line(CYAN, String.format(" %-5s %-10s %-24s %4s %10s %10s %-9s", "NO.", "DATE", "PRODUCT", "QTY", "TOTAL", "CHANGE", "STATUS"));
        for (Sale s : list) {
            String color = s.refunded ? YELLOW : BODY;
            String status = s.refunded ? "REFUNDED" : "PAID";
            line(color, String.format(" #%-4d %-10s %-24s %4d %10.2f %10.2f %-9s",
                    s.id, s.date, fit(s.title, 24), s.quantity, s.total, s.paid - s.total, status));
        }
        if (list.isEmpty()) line(DIM, " No sales yet. Choose 6 to sell something.");
        line(DIM, " Receipts: " + list.size() + "  |  Refunded receipts are yellow.");
        line(BODY, "");
    }

    public void showReport(Bookstore store) {
        double income = 0, stockValue = 0;
        int unitsSold = 0, unitsInStock = 0, lowStock = 0;
        for (Sale s : store.getSales()) {
            if (!s.refunded) {
                income = income + s.total;
                unitsSold = unitsSold + s.quantity;
            }
        }
        for (Product p : store.getProducts()) {
            unitsInStock = unitsInStock + p.getStock();
            stockValue = stockValue + p.getPrice() * p.getStock();
            if (p.getStock() <= 3) lowStock++;
        }
        line(CYAN, String.format(" %-78s", "SALES"));
        line(WHITE, String.format(" %-58s%19.2f ", "Income after refunds (BDT)", income));
        line(BODY, String.format(" %-58s%19d", "Units sold", unitsSold));
        line(BODY, "");
        line(CYAN, String.format(" %-78s", "STOCK"));
        line(BODY, String.format(" %-58s%19d", "Products", store.getProducts().size()));
        line(BODY, String.format(" %-58s%19d", "Low on stock (3 or fewer)", lowStock));
        line(BODY, String.format(" %-58s%19d", "Units in stock", unitsInStock));
        line(BODY, String.format(" %-58s%19d", "New products added this session", Product.getCreatedCount()));
        line(WHITE, String.format(" %-58s%19.2f ", "Stock value at selling price (BDT)", stockValue));
        line(BODY, "");
    }
}
