package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private String loggedInUserId; // To store the logged-in user ID

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    @FXML
    private void handleCategoryAction(ActionEvent event) {
        // Get the selected category from the button text
        String selectedCategory = ((Button) event.getSource()).getText();
        System.out.println("Category Selected: " + selectedCategory);

        try {
            // Load the Menu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oop_cw/Menu.fxml"));
            Parent root = loader.load();

            // Pass the logged-in user ID and selected category to MenuController
            MenuController menuController = loader.getController();
            menuController.setLoggedInUserId(loggedInUserId); // Set user ID in MenuController
            menuController.setCategoryFilter(selectedCategory);

            // Show the Menu.fxml in a new stage
            Stage stage = new Stage();
            stage.setTitle("Articles - " + selectedCategory);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void handleMostLikeAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oop_cw/Recommendation.fxml"));
        Parent root = loader.load();

        RecommendationController recommendationController = loader.getController();
        recommendationController.setLoggedInUserId(loggedInUserId);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleExitAction(ActionEvent event) {
        System.out.println("Exiting Application...");
        System.exit(0); // Exit the application
    }
}
