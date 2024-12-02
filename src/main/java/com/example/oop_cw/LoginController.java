package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Connection connection;

    // Method to handle login validation when the "Login" button is clicked
    @FXML
    protected void OnLoginClick(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        String URL = "jdbc:mysql://localhost:3306/echofeed";
        String USERNAME = "root";

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Both username and password are required.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            connection = DriverManager.getConnection(URL, USERNAME, "");

            // SQL query to check if the username and password exist
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                // Execute query
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Username and password match
                        showAlert("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        // No match found
                        showAlert("Login Failed", "Incorrect username or password.", Alert.AlertType.ERROR);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error: Unable to execute the query.");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Unable to connect to the database.");
            e.printStackTrace();
        } finally {
            // Close the database connection
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    // Helper method to show alert messages
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onBackbuttonclick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Start.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
