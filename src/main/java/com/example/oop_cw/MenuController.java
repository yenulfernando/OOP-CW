package com.example.oop_cw;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @FXML
    private ImageView articleImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button readMoreButton;

    private final List<Article> articles = new ArrayList<>();
    private int currentIndex = 0; // Tracks the current article index
    private String categoryFilter = ""; // Category filter for articles

    @FXML
    public void initialize() {
        loadArticles();
        displayCurrentArticle();
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
        loadArticles();
        displayCurrentArticle();
    }

    private void loadArticles() {
        articles.clear();
        String query = "SELECT article_id, title, content, category, imageUrl FROM Articles";

        if (!categoryFilter.isEmpty()) {
            query += " WHERE category = ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (!categoryFilter.isEmpty()) {
                statement.setString(1, categoryFilter);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int articleId = resultSet.getInt("article_id"); // Fetch article_id
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String category = resultSet.getString("category");
                String imageUrl = resultSet.getString("imageUrl");

                // Add the article to the list
                articles.add(new Article(articleId, title, content, category, imageUrl));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentArticle() {
        if (articles.isEmpty()) {
            titleLabel.setText("No articles available");
            contentLabel.setText("");
            categoryLabel.setText("");
            articleImage.setImage(new Image(getClass().getResource("/placeholder.png").toExternalForm()));
            previousButton.setDisable(true);
            nextButton.setDisable(true);
            readMoreButton.setDisable(true);
        } else {
            Article currentArticle = articles.get(currentIndex);
            titleLabel.setText(currentArticle.getTitle());
            categoryLabel.setText(currentArticle.getCategory());
            articleImage.setImage(currentArticle.getImage());
            contentLabel.setText("Click 'Read More' to view content.");
            contentLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        previousButton.setDisable(currentIndex == 0);
        nextButton.setDisable(currentIndex == articles.size() - 1);
        readMoreButton.setDisable(articles.isEmpty());
    }

    @FXML
    private void onPreviousButtonClicked() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentArticle();
        }
    }

    @FXML
    private void onNextButtonClicked() {
        if (currentIndex < articles.size() - 1) {
            currentIndex++;
            displayCurrentArticle();
        }
    }

    @FXML
    private void onReadMoreButtonClicked() {
        // Show the full content of the current article
        Article currentArticle = articles.get(currentIndex);
        contentLabel.setText(currentArticle.getContent());
        contentLabel.setStyle("-fx-font-style: normal; -fx-text-fill: black;");
        readMoreButton.setDisable(true); // Disable the button after the content is shown
    }

    public static class Article {
        private final int articleId; // Added field for article_id
        private final String title;
        private final String content;
        private final String category;
        private final Image image;

        public Article(int articleId, String title, String content, String category, String imageUrl) {
            this.articleId = articleId; // Initialize the new field
            this.title = title;
            this.content = content;
            this.category = category;
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                this.image = new Image(MenuController.class.getResource("/placeholder.png").toExternalForm());
            } else {
                this.image = new Image(imageUrl, true);
            }
        }

        public int getArticleId() {
            return articleId;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getCategory() {
            return category;
        }

        public Image getImage() {
            return image;
        }
    }
}
