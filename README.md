# Bookstore-JAVA

A console-based Bookstore Management application in Java with a colorful full-screen terminal interface. The code is kept short and uses simple syntax, and it demonstrates every core **Object-Oriented Programming (OOP)** concept from the course.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [How to Compile and Run](#how-to-compile-and-run)
3. [Project File Structure](#project-file-structure)
4. [Mapping of OOP Concepts in Code](#mapping-of-oop-concepts-in-code)
   - [1. Abstraction](#1-abstraction)
   - [2. Encapsulation](#2-encapsulation)
   - [3. Inheritance](#3-inheritance)
   - [4. Polymorphism](#4-polymorphism)
   - [5. Static vs Non-Static](#5-static-vs-non-static)
   - [6. Method Overloading](#6-method-overloading)
   - [7. Method Overriding](#7-method-overriding)
   - [8. 'this' Keyword](#8-this-keyword)
   - [9. 'super' Keyword](#9-super-keyword)
   - [10. Subclass Reference vs Superclass Reference](#10-subclass-reference-vs-superclass-reference)
   - [11. Diamond Problem Resolution](#11-diamond-problem-resolution)
5. [Application Features & Menu Guide](#application-features--menu-guide)
6. [Data Persistence](#data-persistence)
7. [সহজভাবে শিক্ষককে বলার জন্য](#সহজভাবে-শিক্ষককে-বলার-জন্য)

---

## Project Overview

The Bookstore application lets store staff:
- Browse the product catalog (books and stationery); low stock is highlighted.
- Search by ID, title, author, brand or ISBN.
- Add new products with type-specific details.
- Restock items and change prices.
- Sell with an optional 5% member discount, cash and change calculation, and a printed receipt.
- Refund a sale (stock goes back).
- View sales history and a business report (income, units sold, stock value).
- Run a live **OOP Concepts Demo** (type `oop` at the menu).

If an answer is wrong (unknown ID, too little cash, a letter instead of a number…), the program says why and asks that question again. Typing `cancel` stops the current task without changing anything.

---

## How to Compile and Run

**Requirement:** Java Development Kit (JDK) 17 or newer on your PATH.

**Easiest:** double-click `run.bat` (or type `.\run` in a terminal in this folder). It compiles the program and opens it in its own window that is exactly the program's size (80 × 24 characters). Choosing `0 Exit` closes the window.

**By hand:** from this folder, in a terminal at least 80 characters wide:

```powershell
javac *.java
java Main
```

> Java class names are case-sensitive: run `java Main` with a capital **M**.

---

## Project File Structure

```
Bookstore-JAVA/
├── Product.java      # Interfaces Item/Trackable/Promotable, abstract Product, subclasses Book and Stationery
├── Bookstore.java    # Business rules (add, restock, sell, refund, search) and saving/loading; also the Sale class
├── BookstoreUI.java  # Everything on screen: colors, menu tabs, tables, receipt, input questions
├── Main.java         # Starts the program, runs the menu, and the OOP demo
├── run.bat           # Compiles and opens the program in a perfectly sized window
└── README.md         # This guide
```

**How the files work together:** `BookstoreUI` shows a screen and reads the user's answer → `Main` decides which task to run → `Bookstore` checks the rules, changes the data and saves it → `Product` provides the objects being stored and sold → `BookstoreUI` shows the result.

---

## Mapping of OOP Concepts in Code

Every concept below can also be seen **running**: type `oop` at the menu to open the OOP Concepts Demo screen.

---

### 1. Abstraction
> Hiding the details and showing only what is needed.

* **Where:** `Product.java`
* `Product` is an `abstract class`, so `new Product(...)` is not allowed; only a `Book` or `Stationery` can be created.
* It declares abstract methods that every subclass **must** write:
  ```java
  public abstract String getType();
  public abstract String getDetails();
  ```
* Interfaces (`Item`, `Trackable`, `Promotable`) describe behaviour without saying how the class stores its data.

---

### 2. Encapsulation
> Keeping data private and allowing access only through methods.

* **Where:** `Product.java` (and every other class keeps its fields `private`)
* All fields are `private`:
  ```java
  private String id, title;
  private double price;
  private int stock;
  ```
* Getters give read access; setters check the value first:
  ```java
  public void setPrice(double price) {
      if (price >= 0) this.price = price;
  }
  ```

---

### 3. Inheritance
> A subclass gets the fields and methods of its superclass.

* **Where:** `Product.java`
* `Book` and `Stationery` are **subclasses** of the **superclass** `Product`:
  ```java
  class Book extends Product { ... }
  class Stationery extends Product { ... }
  ```
* Both inherit `id`, `title`, `price`, `stock`, `getTitle()`, `setStock()`… and add their own fields (`author`/`isbn` for `Book`, `brand` for `Stationery`).

---

### 4. Polymorphism
> One name, many forms.

* **Compile-time polymorphism** = method overloading → see [6. Method Overloading](#6-method-overloading).
* **Runtime polymorphism** = method overriding + dynamic dispatch. The same call runs a different method depending on the real object. In `BookstoreUI.showProducts`:
  ```java
  for (Product p : list) {
      ... p.getDetails() ...   // runs Book.getDetails() or Stationery.getDetails()
  }
  ```

---

### 5. Static vs Non-Static
> Static members belong to the class; non-static members belong to each object.

* **Where:** `Product.java` (also `Main.java`)

| Type | Member | Explanation |
| :--- | :--- | :--- |
| Static variable | `public static final String CURRENCY;` | One value shared by all products. |
| Static variable | `private static int createdCount = 0;` | Counts every product object created. |
| Static block | `static { CURRENCY = "BDT"; }` | Runs once when the class is loaded. |
| Static method | `public static int getCreatedCount()` | Called on the class: `Product.getCreatedCount()`. |
| Static methods/fields | `Main.sell()`, `Main.store`, `Main.ui` | Belong to the `Main` class; no object needed. |
| Non-static variables | `private double price;`, `private int stock;` | Each product has its own price and stock. |
| Non-static methods | `getPrice()`, `setStock(int)`, all `BookstoreUI` methods | Work on one object's own data. |

---

### 6. Method Overloading
> Same method name, different parameters.

* **Constructor overloading** (`Product.java`):
  ```java
  public Product(String id, String title, double price, int stock) { ... }
  public Product(String id, String title, double price) { this(id, title, price, 0); }

  public Stationery(String id, String title, double price, int stock, String brand) { ... }
  public Stationery(String id, String title, double price, String brand) { ... }
  ```
* **Method overloading** (`Product.java`) — the member discount uses the first one:
  ```java
  public double calculateDiscount(double percentage) { ... }
  public double calculateDiscount(double percentage, double maxDiscount) { ... }
  ```
* **Method overloading** (`Bookstore.java`):
  ```java
  public void restock(String id, int quantity) { restock(get(id), quantity); }
  public void restock(Product p, int quantity) { ... }
  ```
* **Method overloading** (`BookstoreUI.java`): `newScreen(int tab)` and `newScreen(int tab, String title)`.

---

### 7. Method Overriding
> A subclass writes its own version of a method from its superclass or interface, marked with `@Override`.

* **Where:** `Product.java`
  ```java
  // Book
  @Override public String getType() { return "BOOK"; }
  @Override public String getDetails() { return "Book by " + author + ", ISBN " + isbn; }

  // Stationery
  @Override public String getType() { return "STATIONERY"; }
  @Override public String getDetails() { return "Stationery by " + brand; }
  ```
* `Product` also overrides `getCategory()` from its interfaces (see [11](#11-diamond-problem-resolution)).

---

### 8. 'this' Keyword
> `this` means "the current object".

* **Where:** `Product.java`
* Telling a field apart from a parameter with the same name:
  ```java
  this.id = id; // this.id is the object's field, id is the parameter
  ```
* Constructor chaining with `this(...)`:
  ```java
  public Product(String id, String title, double price) {
      this(id, title, price, 0); // calls the 4-value constructor, stock = 0
  }
  ```

---

### 9. 'super' Keyword
> `super` refers to the parent (superclass).

* **Where:** `Product.java`
* Calling the superclass constructor:
  ```java
  public Book(String id, String title, double price, int stock, String author, String isbn) {
      super(id, title, price, stock); // runs the Product constructor first
      this.author = author;
      this.isbn = isbn;
  }
  ```
* Calling a parent interface's version: `Trackable.super.getCategory()` (see [11](#11-diamond-problem-resolution)).

---

### 10. Subclass Reference vs Superclass Reference
> A **subclass reference** (`Book book`) can use everything a Book has. A **superclass reference** (`Product product`) can point to any subclass object (upcasting) but only sees `Product` methods.

* **Where:** `Main.java` → `addProduct()`
  ```java
  Product product;                      // SUPERCLASS reference
  if (type == 'B') {
      Book book = new Book(id, title, price, stock, author, isbn);  // SUBCLASS reference
      extra = " by " + book.getAuthor(); // getAuthor() works only through the Book reference
      product = book;                    // UPCASTING: the Book is stored as a Product
  }
  store.add(product);                    // the inventory stores every item as a Product
  ```

---

### 11. Diamond Problem Resolution
> Java forbids `class C extends A, B` to avoid the diamond problem. With interfaces it can still happen when two interfaces give different `default` versions of the same method.

* **Where:** `Product.java`
  ```
                  Item            default getCategory() -> "Item"
                /      \
         Trackable    Promotable  each overrides getCategory() with its own default
                \      /
                Product           Java forces Product to choose
  ```
* Because `Product implements Trackable, Promotable`, the program does not compile until `Product` overrides the method and picks explicitly with `InterfaceName.super.method()`:
  ```java
  @Override
  public String getCategory() {
      return Trackable.super.getCategory() + " & " + Promotable.super.getCategory();
  }
  ```
* The result (`Tracked stock & Promotable`) is shown on the product card when you restock, change a price or sell.

---

## Application Features & Menu Guide

Every screen has a cyan title bar, menu tabs (the current screen is highlighted), a purple bar with the screen name and clock, and a purple input bar at the bottom. Success messages are green, problems are red, and low-stock rows are yellow with a `!`.

| Option | Screen | What it does |
| :---: | :--- | :--- |
| `1` | Products | Table of all products; low stock (3 or fewer) is yellow and marked `!`. |
| `2` | Search | Finds products by ID, title, author, brand or ISBN. |
| `3` | Add product | Creates a Book or Stationery (shows subclass → superclass upcasting). |
| `4` | Restock | Adds units to a product (uses the overloaded `restock(String, int)`). |
| `5` | Change price | Sets a new unit price. |
| `6` | Sell | Quantity, optional 5% member discount, cash and change, then a paper-style receipt. |
| `7` | Refund | Full refund of a receipt; the stock goes back. A receipt can be refunded only once. |
| `8` | Sales | History of all receipts; refunded ones are yellow. |
| `9` | Report | Income after refunds, units sold, products, low stock, units in stock, stock value. |
| `oop` | OOP demo | Shows all 11 OOP concepts running with two demo objects. |
| `0` | Exit | Closes the program (everything is already saved). |

---

## Data Persistence

Every change is saved straight away to `bookstore.dat` in the program folder, using Java object serialization (`ObjectOutputStream` saves the whole `Bookstore` object, products and sales, in one step; `ObjectInputStream` loads it at start-up). If the file does not exist, the store starts empty. The data file is not uploaded to GitHub (see `.gitignore`).

---

## সহজভাবে শিক্ষককে বলার জন্য

- **Main.java:** পুরো প্রোগ্রাম চালু করে, মেনু চালায় এবং কোন অপশনে কোন কাজ হবে তা ঠিক করে। এখানে OOP ডেমোও আছে।
- **Product.java:** বই ও স্টেশনারি পণ্যের তথ্য এবং তাদের ধরন তৈরি করে। এখানে abstraction, encapsulation, inheritance, overriding, overloading, static, this, super এবং diamond problem দেখানো হয়েছে।
- **Bookstore.java:** পণ্য যোগ, স্টক বাড়ানো, বিক্রি, রিফান্ড এবং ফাইলে তথ্য সংরক্ষণের কাজ করে।
- **BookstoreUI.java:** স্ক্রিন, মেনু, রং, টেবিল দেখানো এবং ব্যবহারকারীর কাছ থেকে ইনপুট নেওয়ার কাজ করে।

**এক লাইনে:** UI ইনপুট/আউটপুট সামলায়, Main কাজটি পরিচালনা করে, Bookstore নিয়ম ও ডেটা সামলায়, আর Product পণ্যের কাঠামো দেয়। মেনুতে `oop` লিখলে সব OOP concept চলমান অবস্থায় দেখা যায়।
