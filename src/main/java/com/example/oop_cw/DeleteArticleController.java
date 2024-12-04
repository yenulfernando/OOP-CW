package com.example.oop_cw;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteArticleController {

    @FXML
    private TableView<Article> articleTable;

    @FXML
    private TableColumn<Article, Integer> colArticleId;

    @FXML
    private TableColumn<Article, String> colTitle;

    @FXML
    private TableColumn<Article, String> colCategory;

    @FXML
    private TableColumn<Article, String> colContent;

    @FXML
    private TableColumn<Article, String> colImageUrl;

    @FXML
    private Button deleteButton;

    private ObservableList<Article> articleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadArticles();

        deleteButton.setOnAction(event -> {
            Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                deleteArticle(selectedArticle.getArticleId());
                articleList.remove(selectedArticle);
            }
        });
    }

    private void loadArticles() {
        articleList.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM articles");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                articleList.add(new Article(
                        resultSet.getInt("article_id"),
                        resultSet.getString("title"),
                        resultSet.getString("category"),
                        resultSet.getString("content"),
                        resultSet.getString("imageUrl")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        colArticleId.setCellValueFactory(new PropertyValueFactory<>("articleId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colImageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        articleTable.setItems(articleList);
    }

    private void deleteArticle(int articleId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM articles WHERE article_id = ?")) {
            statement.setInt(1, articleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inner class for the Article model
    public static class Article {
        private final int articleId;
        private final String title;
        private final String category;
        private final String content;
        private final String imageUrl;

        public Article(int articleId, String title, String category, String content, String imageUrl) {
            this.articleId = articleId;
            this.title = title;
            this.category = category;
            this.content = content;
            this.imageUrl = imageUrl;
        }

        public int getArticleId() {
            return articleId;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getContent() {
            return content;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
