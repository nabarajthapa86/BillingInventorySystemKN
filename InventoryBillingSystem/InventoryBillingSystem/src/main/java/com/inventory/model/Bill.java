package com.inventory.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bill {
    private int billID;
    private Date billDate;
    private double totalAmount;
    private int cashierID;
    private List<BillItem> items;

    public Bill(int billID, int cashierID) {
        this.billID = billID;
        this.cashierID = cashierID;
        this.billDate = new Date();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public int getBillID() { return billID; }
    public void setBillID(int billID) { this.billID = billID; }
    public Date getBillDate() { return billDate; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public int getCashierID() { return cashierID; }
    public void setCashierID(int cashierID) { this.cashierID = cashierID; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public void addItem(BillItem item) {
        items.add(item);
        totalAmount = calculateTotal();
    }

    public void removeItem(int billItemID) {
        items.removeIf(item -> item.getBillItemID() == billItemID);
        totalAmount = calculateTotal();
    }

    public double calculateTotal() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
