# Inventory & Billing System

Java Swing desktop application — Aadikavi Bhanubhakta Campus project.

---

## Project Structure

```
src/main/java/com/inventory/
├── Main.java                   ← Entry point
├── model/
│   ├── User.java               ← Abstract base class
│   ├── Admin.java
│   ├── Cashier.java
│   ├── Product.java
│   ├── Bill.java
│   └── BillItem.java
├── dao/
│   ├── AdminDAO.java
│   ├── CashierDAO.java
│   ├── ProductDAO.java
│   └── BillDAO.java
├── ui/
│   ├── LoginFrame.java
│   ├── AdminDashboard.java
│   └── CashierDashboard.java
└── util/
    └── DBConnection.java

database/
└── schema.sql                  ← Run this in phpMyAdmin first
```

---

## Setup Steps

### 1. Database (XAMPP)
1. Start XAMPP and turn on **Apache** and **MySQL**
2. Open **phpMyAdmin** → http://localhost/phpmyadmin
3. Click **Import** → choose `database/schema.sql` → click Go
4. This creates the `inventory_billing` database with all tables and a default admin account

### 2. IntelliJ IDEA
1. Open IntelliJ → **File → Open** → select the `InventoryBillingSystem` folder
2. Go to **File → Project Structure → Libraries → + → Java**
3. Add the MySQL JDBC driver JAR:
   - Download `mysql-connector-j-8.x.x.jar` from https://dev.mysql.com/downloads/connector/j/
   - Or find it at `C:\xampp\mysql\lib\` (on Windows XAMPP)
4. Set **Main class** to `com.inventory.Main`
5. Run the project

### 3. Default Login
| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | admin    | admin123  |

---

## Features
- **Admin**: manage cashier accounts (create/edit/delete/view), manage products (add/edit/delete/view stock)
- **Cashier**: create bills, add/remove items, auto stock deduction, print receipt, view past bills
