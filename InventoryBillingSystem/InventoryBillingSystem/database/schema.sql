-- Run this in phpMyAdmin (XAMPP) before starting the application

CREATE DATABASE IF NOT EXISTS inventory_billing;
USE inventory_billing;

CREATE TABLE IF NOT EXISTS admin (
    adminID   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS cashier (
    cashierID INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    productID INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    price     DOUBLE NOT NULL,
    stockQty  INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bill (
    billID      INT AUTO_INCREMENT PRIMARY KEY,
    billDate    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    totalAmount DOUBLE NOT NULL DEFAULT 0.0,
    cashierID   INT NOT NULL,
    FOREIGN KEY (cashierID) REFERENCES cashier(cashierID)
);

CREATE TABLE IF NOT EXISTS bill_items (
    billItemID      INT AUTO_INCREMENT PRIMARY KEY,
    billID          INT NOT NULL,
    productID       INT NOT NULL,
    quantitySold    INT NOT NULL,
    unitPriceAtSale DOUBLE NOT NULL,
    FOREIGN KEY (billID)    REFERENCES bill(billID),
    FOREIGN KEY (productID) REFERENCES product(productID)
);

-- Default admin account (username: admin, password: admin123)
INSERT INTO admin (username, password) VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE username = username;
