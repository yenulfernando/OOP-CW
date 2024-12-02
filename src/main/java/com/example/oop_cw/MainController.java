package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private void handleCategoryAction(ActionEvent event) {
        // Get the selected category from the button text
        String selectedCategory = ((Button) event.getSource()).getText();
        System.out.println("Category Selected: " + selectedCategory);

        try {
            // Load the Menu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/oop_cw/Menu.fxml"));
            Parent root = loader.load();

            // Pass the selected category to the MenuController
            MenuController menuController = loader.getController();
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
    private void handleUserHistoryAction(ActionEvent event) {
        System.out.println("See User History clicked!");
        // Add logic for "See User History" button
    }

    @FXML
    private void handleMostLikeAction(ActionEvent event) {
        System.out.println("See What You Most Like clicked!");
        // Add logic for "See What You Most Like" button

    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.out.println("Exiting Application...");
        System.exit(0); // Exit the application
    }
}
