package com.inventory.model;

public class Admin extends User {
    private int adminID;

    public Admin(int adminID, String username, String password) {
        super(username, password);
        this.adminID = adminID;
    }

    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }

    @Override
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Admin logged out.");
    }
}