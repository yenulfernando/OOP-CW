package com.example.oop_cw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/echofeed";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Replace with your database password

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load JDBC driver
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found.", e);
        }
    }
}
