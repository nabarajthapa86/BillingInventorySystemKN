package com.inventory.model;

public class Cashier extends User {
    private int cashierID;

    public Cashier(int cashierID, String username, String password) {
        super(username, password);
        this.cashierID = cashierID;
    }

    public int getCashierID() { return cashierID; }
    public void setCashierID(int cashierID) { this.cashierID = cashierID; }

    @Override
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Cashier logged out.");
    }
}
