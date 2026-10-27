# Bookstore-JAVA

A clean, easy-to-understand console-based Bookstore Management application built in Java. This project demonstrates core **Object-Oriented Programming (OOP)** principles in an accessible, practical format without unnecessary complexity.

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

---

## Project Overview

The Bookstore application enables store staff to:
- Browse the current product catalog (books & stationery).
- Add new products with type-specific attributes.
- Restock existing inventory.
- Update selling prices.
- Process customer checkouts with optional membership discounts and cash change calculation.
- Review past transaction receipts.
- View real-time business financial summaries (total inventory value, revenue, net sales).
- Run an interactive on-screen **OOP Demonstration**.

---

## How to Compile and Run

### Prerequisites
- **Java Development Kit (JDK 17 or higher)** installed and added to your system PATH.

### Steps

1. **Open PowerShell or Command Prompt** in the project directory:
   ```powershell
   cd "A:\bookstore-java (1)"
   ```

2. **Compile all Java source files**:
   ```powershell
   javac Main.java Product.java Bookstore.java
   ```

3. **Run the program**:
   ```powershell
   java Main
   ```
   > **Note:** Java class names are case-sensitive. Always run with a capital **`M`** (`java Main`), not `java main`.

---

## Project File Structure

```
A:\bookstore-java (1)\
│
├── Product.java         # Base abstract class, subclasses (Book, Stationery), and diamond interfaces
├── Bookstore.java       # Inventory, sales management, business logic, and file I/O
├── Main.java            # Text-based console UI, interactive menu, input handling, and OOP demo
├── bookstore_data.txt   # Automatic text-based storage file for inventory & sales
└── README.md            # Complete project documentation and OOP concept guide
```

---

## Mapping of OOP Concepts in Code

Here is the exact mapping of where and how each required OOP concept is implemented in the source code:

---

### 1. Abstraction
> **Definition:** Hiding internal implementation details and exposing only essential interfaces and behaviors to the outside world.

* **Where it is used:** `Product.java`
* **Implementation:**
  1. `Product` is defined as an `abstract class`. It cannot be directly instantiated (`new Product(...)` is prohibited).
  2. Declares abstract method signatures that every concrete product must provide:
     ```java
     public abstract String getType();
     public abstract String getDetails();
     ```
  3. Uses interfaces (`Item`, `Trackable`, `Promotable`) to define behavioral contracts.

---

### 2. Encapsulation
> **Definition:** Restricting direct access to an object's internal components and state, exposing controlled access via getters and setters with validation.

* **Where it is used:** `Product.java`, `Bookstore.java`
* **Implementation:**
  1. All instance variables are declared `private`:
     ```java
     // Product.java
     private String id;
     private String title;
     private double price;
     private int stock;

     // Book subclass
     private String author;
     private String isbn;
     ```
  2. Controlled access provided via public getters and setters with data validation:
     ```java
     public void setPrice(double price) {
         if (price >= 0) {
             this.price = price;
         }
     }
     ```

---

### 3. Inheritance
> **Definition:** Mechanism where a subclass inherits state and behaviors from a superclass, promoting code reuse.

* **Where it is used:** `Product.java`
* **Implementation:**
  1. `Book` inherits from `Product`:
     ```java
     public static class Book extends Product { ... }
     ```
  2. `Stationery` inherits from `Product`:
     ```java
     public static class Stationery extends Product { ... }
     ```
  Both subclasses automatically inherit `id`, `title`, `price`, `stock`, `getId()`, `setStock()`, and add their own specialized fields (`author`/`isbn` for `Book`, `brand` for `Stationery`).

---

### 4. Polymorphism
> **Definition:** The ability of an object, reference, or method to take on multiple forms (Compile-Time and Runtime).

#### A. Compile-Time Polymorphism (Method Overloading)
* Multiple methods/constructors with the same name but different parameter lists.
* See [Section 6: Method Overloading](#6-method-overloading).

#### B. Runtime Polymorphism (Method Overriding & Dynamic Dispatch)
* Calling overridden methods on a superclass reference dynamically executes the subclass's version at runtime.
* In `Main.java`:
  ```java
  for (Product p : store.getInventory()) {
      // Dynamic method dispatch: executes Book.getDetails() or Stationery.getDetails()
      System.out.println(p.getDetails()); 
  }
  ```

---

### 5. Static vs Non-Static
> **Definition:** Static members belong to the class as a whole, while non-static (instance) members belong to individual object instances.

* **Where it is used:** `Product.java`

| Type | Member | Explanation |
| :--- | :--- | :--- |
| **Static Variable** | `public static final String CURRENCY = "BDT";` | Shared by all instances of `Product`. |
| **Static Variable** | `private static int totalProductsCount = 0;` | Class-level counter tracking total products created across the whole application. |
| **Static Block** | `static { /* class initialization */ }` | Executes once when the class is loaded into memory. |
| **Static Method** | `public static int getTotalProductsCount()` | Called directly on the class (`Product.getTotalProductsCount()`) without needing an instance. |
| **Non-Static Variable** | `private double price;`, `private int stock;` | Each product instance maintains its own distinct price and stock. |
| **Non-Static Method** | `public double getPrice()`, `public void setStock(int stock)` | Operates directly on the caller object's specific data. |

---

### 6. Method Overloading
> **Definition:** Defining multiple methods in the same class with the same name but different parameter counts or parameter types.

* **Where it is used:** `Product.java`, `Bookstore.java`
* **Implementation:**
  1. **Constructor Overloading (`Product.java`):**
     ```java
     // Overload 1: 4 parameters
     public Product(String id, String title, double price, int stock) { ... }

     // Overload 2: 3 parameters (defaults stock to 0)
     public Product(String id, String title, double price) { ... }
     ```
  2. **Method Overloading in `Product.java`:**
     ```java
     // Overload 1: Calculate discount by percentage only
     public double calculateDiscount(double percentage) {
         return (this.price * percentage) / 100.0;
     }

     // Overload 2: Calculate discount by percentage AND maximum cap limit
     public double calculateDiscount(double percentage, double maxCap) {
         double discount = calculateDiscount(percentage);
         return Math.min(discount, maxCap);
     }
     ```
  3. **Method Overloading in `Bookstore.java`:**
     ```java
     // Overload 1: Restock by product ID String
     public void restock(String id, int quantity) { ... }

     // Overload 2: Restock by Product reference
     public void restock(Product product, int quantity) {
         restock(product.getId(), quantity);
     }
     ```

---

### 7. Method Overriding
> **Definition:** A subclass providing a specific implementation of a method that is already declared in its superclass or interface using the `@Override` annotation.

* **Where it is used:** `Product.java` (`Book` and `Stationery` subclasses)
* **Implementation:**
  ```java
  // In Book.java:
  @Override
  public String getType() {
      return "BOOK";
  }

  @Override
  public String getDetails() {
      return "Author: " + this.author + " | ISBN: " + this.isbn;
  }

  // In Stationery.java:
  @Override
  public String getType() {
      return "STATIONERY";
  }

  @Override
  public String getDetails() {
      return "Brand: " + this.brand;
  }
  ```

---

### 8. 'this' Keyword
> **Definition:** A reference variable that refers to the current object instance.

* **Where it is used:** `Product.java`, `Bookstore.java`
* **Implementation:**
  1. **Resolving Variable Shadowing:** Differentiates instance variables from constructor parameters:
     ```java
     public Product(String id, String title, double price, int stock) {
         this.id = id;         // this.id is the field; id is the parameter
         this.title = title;
         this.price = price;
         this.stock = stock;
     }
     ```
  2. **Constructor Chaining (`this(...)`):** Calls another constructor in the same class:
     ```java
     public Product(String id, String title, double price) {
         this(id, title, price, 0); // Calls the 4-parameter constructor
     }
     ```

---

### 9. 'super' Keyword
> **Definition:** A reference variable used to refer directly to the immediate parent class object.

* **Where it is used:** `Product.java`
* **Implementation:**
  1. **Invoking Superclass Constructor:** Subclasses `Book` and `Stationery` pass shared attributes to `Product` via `super(...)`:
     ```java
     public Book(String id, String title, double price, int stock, String author, String isbn) {
         super(id, title, price, stock); // Calls Product constructor
         this.author = author;
         this.isbn = isbn;
     }
     ```

---

### 10. Subclass Reference vs Superclass Reference
> **Definition:** 
> - **Subclass Reference:** Reference typed as the concrete child class; can access subclass-specific methods.
> - **Superclass Reference:** Reference typed as the parent class; points to child object (Upcasting), enabling polymorphic method dispatch.

* **Where it is used:** `Main.java` (`addProduct()` method)
* **Implementation:**
  ```java
  // 1. SUBCLASS REFERENCE:
  // Typed as Product.Book -> gives access to Book-specific getters
  Product.Book book = new Product.Book(id, title, price, stock, author, isbn);
  System.out.println("Author: " + book.getAuthor()); // Accessible via subclass reference

  // 2. SUPERCLASS REFERENCE (Upcasting):
  // Typed as base class Product -> allows storing in generic inventory
  Product product = book; 
  store.addProduct(product);
  ```

---

### 11. Diamond Problem Resolution
> **Definition:** The Diamond Problem occurs when a class inherits from two sources that provide conflicting implementations of the same method. 
> 
> In Java, multiple class inheritance (`class C extends A, B`) is prohibited to prevent this issue. However, multiple interface inheritance with `default` methods creates an interface-level diamond.

* **Where it is used:** `Product.java`
* **The Structure:**
  ```
                 Item (default getCategory())
                  /  \
         Trackable    Promotable  (both override getCategory() with default implementations)
                  \  /
                 Product (COMPILER FORCES Product to override and resolve!)
  ```
* **Implementation in `Product.java`:**
  ```java
  interface Item {
      default String getCategory() { return "General Item"; }
  }

  interface Trackable extends Item {
      @Override
      default String getCategory() { return "Trackable Inventory"; }
  }

  interface Promotable extends Item {
      @Override
      default String getCategory() { return "Promotional Merchandise"; }
  }
  ```
* **Explicit Resolution in `Product`:**
  Because both `Trackable` and `Promotable` provide default implementations of `getCategory()`, the Java compiler refuses to compile until `Product` overrides the method and explicitly resolves the conflict using `InterfaceName.super.methodName()`:
  ```java
  public abstract class Product implements Trackable, Promotable {
      @Override
      public String getCategory() {
          // Explicitly resolves diamond ambiguity:
          return Trackable.super.getCategory() + " & " + Promotable.super.getCategory();
      }
  }
  ```

---

## Application Features & Menu Guide

When you launch `java Main`, you are greeted with this menu:

```
====================================================================
                   WELCOME TO JAVA BOOKSTORE                        
====================================================================
--------------------------------------------------------------------
 [1] View Inventory     [2] Add Product       [3] Restock Product
 [4] Change Price       [5] Sell / Checkout   [6] Sales History
 [7] Business Reports   [8] OOP Concepts Demo [0] Exit
--------------------------------------------------------------------
```

| Option | Shortcut | Functionality |
| :---: | :--- | :--- |
| `[1]` | `inv`, `inventory` | Displays table of all inventory items, prices, quantities, details, and diamond-problem tags. |
| `[2]` | `add`, `a` | Step-by-step form to create a new Book or Stationery item (demonstrates subclass reference to superclass reference upcasting). |
| `[3]` | `restock`, `r` | Restocks quantity of an existing item using overloaded `store.restock(...)`. |
| `[4]` | `price` | Updates the unit selling price of any product. |
| `[5]` | `sell`, `checkout` | Sells products, applies optional 5% member discount, validates cash received, and prints change returned. |
| `[6]` | `sales` | Lists all past issued sales receipts with totals and change amounts. |
| `[7]` | `reports`, `rep` | Shows financial overview: total unique titles, total units in stock, total inventory asset value, total units sold, and net revenue. |
| `[8]` | `demo`, `oop` | Runs a live interactive demonstration showcasing all OOP concepts and their console outputs. |
| `[0]` | `exit`, `quit` | Saves all data and safely exits the program. |

---

## Data Persistence

All inventory items and completed sales receipts are automatically saved to:
`bookstore_data.txt`

When the application starts, it reads existing data from this file. If the file does not exist, it initializes with a default starter catalog (`Clean Code`, `Effective Java`, `Pilot Gel Pen Pack`).
