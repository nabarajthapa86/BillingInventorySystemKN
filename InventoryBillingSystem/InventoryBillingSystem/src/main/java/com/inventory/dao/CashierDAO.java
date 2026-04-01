package com.inventory.dao;

import com.inventory.model.Cashier;
import com.inventory.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CashierDAO {

    public boolean create(String username, String password) {
        String sql = "INSERT INTO cashier (c_username, c_password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int cashierID, String username, String password) {
        String sql = "UPDATE cashier SET c_username = ?, c_password = ? WHERE cashierID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, cashierID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int cashierID) {
        String sql = "DELETE FROM cashier WHERE cashierID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cashierID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cashier> getAll() {
        List<Cashier> list = new ArrayList<>();
        String sql = "SELECT * FROM cashier";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Cashier(rs.getInt("cashierID"), rs.getString("c_username"), rs.getString("c_password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Cashier login(String username, String password) {
        String sql = "SELECT * FROM cashier WHERE c_username = ? AND c_password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cashier(rs.getInt("cashierID"), rs.getString("c_username"), rs.getString("c_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}