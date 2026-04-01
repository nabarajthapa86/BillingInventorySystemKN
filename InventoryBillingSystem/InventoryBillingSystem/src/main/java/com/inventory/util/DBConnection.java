package com.inventory.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL      = "jdbc:mysql://localhost:3306/inventory_billing";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private DBConnection() {}

    /**
     * Returns a NEW connection each time.
     * Always use try-with-resources: try (Connection conn = DBConnection.getConnection()) { ... }
     * This avoids the "ResultSet closed" error that occurs when two queries share one connection.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
}