package com.inventory.model;

public class BillItem {
    private int billItemID;
    private int billID;
    private int productID;
    private String productName;
    private int quantitySold;
    private double unitPriceAtSale;

    public BillItem(int billItemID, int billID, int productID, String productName,
                    int quantitySold, double unitPriceAtSale) {
        this.billItemID = billItemID;
        this.billID = billID;
        this.productID = productID;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.unitPriceAtSale = unitPriceAtSale;
    }

    public int getBillItemID() { return billItemID; }
    public void setBillItemID(int billItemID) { this.billItemID = billItemID; }
    public int getBillID() { return billID; }
    public void setBillID(int billID) { this.billID = billID; }
    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
    public double getUnitPriceAtSale() { return unitPriceAtSale; }
    public void setUnitPriceAtSale(double unitPriceAtSale) { this.unitPriceAtSale = unitPriceAtSale; }

    public double getSubtotal() {
        return quantitySold * unitPriceAtSale;
    }
}
