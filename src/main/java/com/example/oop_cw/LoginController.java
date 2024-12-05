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
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private String loggedInUserId; // To store the logged-in user ID

    @FXML
    protected void OnLoginClick(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        String URL = "jdbc:mysql://localhost:3306/echofeed";
        String USERNAME = "root";

        // Validation: Ensure username and password are not empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Both username and password are required.", Alert.AlertType.ERROR);
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, "")) {
            // SQL query to verify login credentials
            String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Retrieve user ID upon successful login
                        loggedInUserId = resultSet.getString("user_id");
                        showAlert("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);

                        // Load Main.fxml after login
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oop_cw/Main.fxml"));
                        Parent root = loader.load();

                        // Pass the logged-in user ID to the MainController
                        MainController mainController = loader.getController();
                        mainController.setLoggedInUserId(loggedInUserId);

                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        showAlert("Login Failed", "Incorrect username or password.", Alert.AlertType.ERROR);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onBackbuttonclick(ActionEvent event) throws IOException {
        // Go back to the start screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oop_cw/Start.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
