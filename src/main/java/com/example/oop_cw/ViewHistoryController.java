package com.example.oop_cw;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewHistoryController {

    @FXML
    private ListView<String> historyListView;

    private int currentUserId = 1; // Replace with dynamic user ID handling logic

    @FXML
    public void initialize() {
        loadUserHistory();
    }

    private void loadUserHistory() {
        String query = "SELECT a.title FROM user_history uh JOIN Articles a ON uh.article_id = a.article_id WHERE uh.user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentUserId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                historyListView.getItems().add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
