package com.inventory.model;

public class Product {
    private int productID;
    private String name;
    private double price;
    private int stockQty;

    public Product(int productID, String name, double price, int stockQty) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.stockQty = stockQty;
    }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }

    public boolean updateStock(int qty) {
        if (stockQty + qty < 0) return false;
        this.stockQty += qty;
        return true;
    }

    public boolean isAvailable() {
        return stockQty > 0;
    }

    @Override
    public String toString() {
        return name + " (Rs. " + price + ")";
    }
}
