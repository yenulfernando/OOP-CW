package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RecommendationController {

    @FXML
    private VBox recommendationsContainer;

    private String loggedInUserId;

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        recommendArticles();
    }

    private void recommendArticles() {
        // Identify the user's most liked category
        String favoriteCategory = identifyFavoriteCategory();

        if (favoriteCategory != null) {
            // Load recommended articles for the identified category
            List<Article> recommendedArticles = fetchRecommendedArticles(favoriteCategory);

            // Display the recommended articles
            displayRecommendedArticles(recommendedArticles);
        } else {
            // Show no recommendations available
            Label noRecommendationsLabel = new Label("No recommendations available at the moment.");
            recommendationsContainer.getChildren().add(noRecommendationsLabel);
        }
    }

    private String identifyFavoriteCategory() {
        Map<String, Integer> categoryCount = new HashMap<>();

        // Query user history to analyze the liked categories
        String query = "SELECT category FROM user_history WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(loggedInUserId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Use pre-trained model logic (simulated) to predict the most liked category
        return predictFavoriteCategory(categoryCount);
    }

    private String predictFavoriteCategory(Map<String, Integer> categoryCount) {
        String modelPath = "C:\\Users\\yenul\\IdeaProjects\\oop_cw\\src\\main\\resources\\com\\example\\oop_cw\\ml_model.dat";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath))) {
            @SuppressWarnings("unchecked")
            Map<String, Double> trainedModel = (Map<String, Double>) ois.readObject();

            // Calculate scores for each category using the trained model
            String predictedCategory = null;
            double maxScore = -1;

            for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                String category = entry.getKey();
                int count = entry.getValue();
                double weight = trainedModel.getOrDefault(category, 1.0);

                double score = count * weight;
                if (score > maxScore) {
                    maxScore = score;
                    predictedCategory = category;
                }
            }

            return predictedCategory;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Article> fetchRecommendedArticles(String category) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT article_id, title, content, imageUrl FROM articles WHERE category = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("article_id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String imageUrl = resultSet.getString("imageUrl");

                articles.add(new Article(articleId, title, content, category, imageUrl));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    private void displayRecommendedArticles(List<Article> articles) {
        recommendationsContainer.getChildren().clear();

        for (Article article : articles) {
            VBox articleBox = new VBox();

            // Add title
            Label titleLabel = new Label(article.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            articleBox.getChildren().add(titleLabel);

            // Add image
            ImageView articleImage = new ImageView(article.getImage());
            articleImage.setFitWidth(300);
            articleImage.setFitHeight(200);
            articleBox.getChildren().add(articleImage);

            // Add short content preview
            Label contentLabel = new Label(article.getContent().substring(0, Math.min(article.getContent().length(), 100)) + "...");
            articleBox.getChildren().add(contentLabel);

            recommendationsContainer.getChildren().add(articleBox);
        }
    }

    @FXML
    protected void onBackButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
                    ? new Image(getClass().getResource("/placeholder.png").toExternalForm())
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

    public static class MLModelTrainer {
        public static void main(String[] args) {
            Map<String, Double> trainedModel = new HashMap<>();
            trainedModel.put("Technology", 1.5);
            trainedModel.put("Health", 1.2);
            trainedModel.put("Sports", 1.3);
            trainedModel.put("Politics", 0.8);
            trainedModel.put("AI", 1.7);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\yenul\\IdeaProjects\\oop_cw\\src\\main\\resources\\com\\example\\oop_cw\\ml_model.dat"))) {
                oos.writeObject(trainedModel);
                System.out.println("Model trained and saved.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
