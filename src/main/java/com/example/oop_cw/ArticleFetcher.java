package com.example.oop_cw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.json.JSONArray;
import org.json.JSONObject;

public class ArticleFetcher {
    private static final String API_KEY = "80b4f6d7cedf46759f825424f7a240a2";
    private static final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&category=science&apiKey=";

    // Method to fetch articles from the API
    public static String fetchArticles() {
        try {
            URL url = new URL(API_URL + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // Success
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString(); // JSON string of articles
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to process and save articles to the database
    public static void processAndSaveArticles(String articlesJson, int userId) {
        if (articlesJson == null) {
            System.out.println("No articles to process.");
            return;
        }

        // Parse the JSON response
        JSONObject jsonObject = new JSONObject(articlesJson);
        JSONArray articles = jsonObject.getJSONArray("articles");

        // Database connection and query
        String insertQuery = "INSERT INTO Articles (title, content, category, imageUrl) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Loop through each article and insert into the database
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);

                // Extract relevant fields
                String title = article.optString("title");
                String content = article.optString("description"); // or "content"
                String category = "AI"; // Default category (you can modify this logic)
                String imageUrl = article.optString("urlToImage");

                // Set values in the PreparedStatement
                statement.setString(1, title);
                statement.setString(2, content);
                statement.setString(3, category);
                statement.setString(4, imageUrl);


                System.out.println("Title: " + title);
                System.out.println("Content: " + content);
                System.out.println("Category: " + category);
                System.out.println("Image URL: " + imageUrl); // Display the image URL
                System.out.println();

                // Execute the insert query
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Inserted: " + title);
                } else {
                    System.out.println("Failed to insert: " + title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage: Fetch articles and save for a specific user ID (e.g., user_id = 1)
        int userId = 1; // Replace with the actual user ID
        String articlesJson = fetchArticles();
        processAndSaveArticles(articlesJson, userId);
    }
}
