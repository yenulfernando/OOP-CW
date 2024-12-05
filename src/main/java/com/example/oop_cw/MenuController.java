package com.example.oop_cw;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private Button NextButton;
    @FXML
    private Button readMoreButton;
    @FXML
    private Button SkipButton;

    private final List<Article> articles = new ArrayList<>();
    private int currentIndex = 0; // Tracks the current article index
    private String categoryFilter = ""; // Category filter for articles
    private String loggedInUserId; // To store the logged-in user ID

    @FXML
    public void initialize() {
        loadArticles();
        displayCurrentArticle();
    }

    public void setLoggedInUserId(String userId) {
        this.loggedInUserId = userId;
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
            NextButton.setDisable(true);
            readMoreButton.setDisable(true);
            SkipButton.setDisable(true);
        } else {
            Article currentArticle = articles.get(currentIndex);
            titleLabel.setText(currentArticle.getTitle());
            categoryLabel.setText(currentArticle.getCategory());
            articleImage.setImage(currentArticle.getImage());
            contentLabel.setText("If you like this article, click 'Read More' to view content. Otherwise, skip the article.");
            contentLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

            // Enable Read More and Skip buttons for the current article
            readMoreButton.setDisable(false);
            SkipButton.setDisable(false);

            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        NextButton.setDisable(currentIndex == articles.size() - 1);
    }

    @FXML
    private void onNextButtonClicked() {
        if (currentIndex < articles.size() - 1) {
            currentIndex++;
            displayCurrentArticle();
        } else {
            // Disable Next button if no more articles are available
            NextButton.setDisable(true);
        }
    }

    @FXML
    private void onReadMoreButtonClicked() {
        // Show the full content of the current article
        Article currentArticle = articles.get(currentIndex);
        contentLabel.setText(currentArticle.getContent());
        contentLabel.setStyle("-fx-font-style: normal; -fx-text-fill: black;");

        // Disable both Read More and Skip buttons after Read More is clicked
        readMoreButton.setDisable(true);
        SkipButton.setDisable(true);

        // Save the article to the user history table
        if (loggedInUserId != null && !loggedInUserId.isEmpty()) {
            saveArticleToHistory(currentArticle);
        }
    }

    @FXML
    private void onSkipButtonClick() {
        // Disable Read More button after Skip is clicked
        readMoreButton.setDisable(true);

        // Move to the next article automatically
        if (currentIndex < articles.size() - 1) {
            currentIndex++;
            displayCurrentArticle();
        } else {
            // If no more articles are left, indicate the end of the list
            titleLabel.setText("No more articles to display");
            contentLabel.setText("");
            categoryLabel.setText("");
            articleImage.setImage(new Image(getClass().getResource("/placeholder.png").toExternalForm()));
            NextButton.setDisable(true);
            readMoreButton.setDisable(true);
            SkipButton.setDisable(true);
        }
    }

    private void saveArticleToHistory(Article article) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO user_history (user_id, article_id, category, read_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, Integer.parseInt(loggedInUserId)); // Logged-in user ID
            preparedStatement.setInt(2, article.getArticleId()); // Selected article ID
            preparedStatement.setString(3, article.getCategory());

            preparedStatement.executeUpdate();
            System.out.println("Article saved to user history for User ID: " + loggedInUserId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Article {
        private final int articleId;
        private final String title;
        private final String content;
        private final String category;
        private final Image image;

        public Article(int articleId, String title, String content, String category, String imageUrl) {
            this.articleId = articleId;
            this.title = title;
            this.content = content;
            this.category = category;
            this.image = imageUrl == null || imageUrl.trim().isEmpty()
                    ? new Image(MenuController.class.getResource("/placeholder.png").toExternalForm())
                    : new Image(imageUrl, true);
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
