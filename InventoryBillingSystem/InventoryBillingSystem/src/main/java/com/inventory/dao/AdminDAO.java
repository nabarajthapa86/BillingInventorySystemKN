package com.inventory.dao;

import com.inventory.model.Admin;
import com.inventory.util.DBConnection;

import java.sql.*;

public class AdminDAO {

    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admin WHERE a_username = ? AND a_password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("adminID"), rs.getString("a_username"), rs.getString("a_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}