package com.inventory.dao;

import com.inventory.model.Bill;
import com.inventory.model.BillItem;
import com.inventory.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public int saveBill(Bill bill) {
        String sql = "INSERT INTO bill (billDate, totalAmount, cashierID) VALUES (NOW(), ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, bill.getTotalAmount());
            ps.setInt(2, bill.getCashierID());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedID = rs.getInt(1);
                bill.setBillID(generatedID);
                for (BillItem item : bill.getItems()) {
                    saveBillItem(item, generatedID);
                }
                return generatedID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void saveBillItem(BillItem item, int billID) {
        String sql = "INSERT INTO bill_items (billID, productID, quantitySold, unitPriceAtSale) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billID);
            ps.setInt(2, item.getProductID());
            ps.setInt(3, item.getQuantitySold());
            ps.setDouble(4, item.getUnitPriceAtSale());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Bill> getBillsByCashier(int cashierID) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE cashierID = ? ORDER BY billDate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cashierID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill b = new Bill(rs.getInt("billID"), rs.getInt("cashierID"));
                    b.setBillDate(rs.getTimestamp("billDate"));
                    b.setTotalAmount(rs.getDouble("totalAmount"));
                    bills.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Bill b : bills) {
            b.setItems(getItemsByBillID(b.getBillID()));
        }
        return bills;
    }

    public Bill getBillByID(int billID) {
        String sql = "SELECT * FROM bill WHERE billID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = new Bill(rs.getInt("billID"), rs.getInt("cashierID"));
                b.setBillDate(rs.getTimestamp("billDate"));
                b.setTotalAmount(rs.getDouble("totalAmount"));
                b.setItems(getItemsByBillID(billID));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BillItem> getItemsByBillID(int billID) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT bi.*, p.name FROM bill_items bi JOIN product p ON bi.productID = p.productID WHERE bi.billID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new BillItem(
                        rs.getInt("billItemID"),
                        billID,
                        rs.getInt("productID"),
                        rs.getString("name"),
                        rs.getInt("quantitySold"),
                        rs.getDouble("unitPriceAtSale")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}