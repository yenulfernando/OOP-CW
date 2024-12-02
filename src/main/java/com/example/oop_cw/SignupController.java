package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    // FXML fields (bind these to the FXML file)
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox<String> preferenceChoiceBox;

    private Connection connection;

    // Method to handle signup validation when the "Continue" button is clicked
    @FXML
    protected void OnContinueClick(ActionEvent event) {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String preference = preferenceChoiceBox.getValue();

        String URL = "jdbc:mysql://localhost:3306/echofeed";
        String USERNAME = "root";

        // Check if any field is empty or null
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || preference == null) {
            showAlert("Validation Error", "All fields must be filled!", AlertType.ERROR);
            return;
        }

        // Validate password: should contain both numeric and alphabetic values
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            showAlert("Validation Error", "Password must contain both letters and numbers.", AlertType.ERROR);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Validation Error", "Please enter a valid email address.", AlertType.ERROR);
            return;
        }

        // Database connection and insertion
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, "");
            System.out.println("Connected to the database.");

            String sql = "INSERT INTO users (name, username, email, password, preference) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, password);
                preparedStatement.setString(5, preference);

                // Execute the update
                int rowsAffected = preparedStatement.executeUpdate(); // Executes the insert statement

                if (rowsAffected > 0) {
                    showAlert("Success", "Signup successful!", AlertType.INFORMATION);
                    clearFields(); // Clear all fields
                } else {
                    showAlert("Error", "Data insertion failed.", AlertType.ERROR);
                }
                System.out.println("Rows affected: " + rowsAffected);
            } catch (SQLException e) {
                System.out.println("Error: Unable to execute the insert statement.");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Unable to connect to the database.");
            e.printStackTrace();
        } finally {
            // Close the connection
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

    // Helper method to clear all input fields
    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        preferenceChoiceBox.setValue(null);
    }

    // Helper method to show alert messages
    private void showAlert(String title, String message, AlertType alertType) {
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
