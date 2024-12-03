package com.example.oop_cw;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AddArticleController {

    @FXML
    private TextField articleid;
    @FXML
    private TextArea articletitle;
    @FXML
    private TextArea articlecontent;
    private String imageUrl;

    private Classifier classifier;
    private Instances filteredData;
    private Instances dataset;

    public AddArticleController() {
        try {
            initializeClassifier();
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize classifier: " + e.getMessage());
        }
    }

    // Initialize Weka classifier
    private void initializeClassifier() throws Exception {
        List<String> contents = DatabaseConnection.getNewsData("articles", "content");
        List<String> categories = DatabaseConnection.getNewsData("articles", "category");

        FastVector attributes = new FastVector();
        attributes.addElement(new Attribute("content", (FastVector) null)); // Text attribute
        FastVector classValues = new FastVector();

        for (String category : categories) {
            if (!classValues.contains(category)) {
                classValues.addElement(category);
            }
        }
        attributes.addElement(new Attribute("category", classValues)); // Class attribute

        dataset = new Instances("NewsDataset", attributes, contents.size());
        dataset.setClassIndex(1); // Class attribute index

        for (int i = 0; i < contents.size(); i++) {
            Instance instance = new DenseInstance(2);
            instance.setValue((Attribute) attributes.elementAt(0), contents.get(i));
            instance.setValue((Attribute) attributes.elementAt(1), categories.get(i));
            dataset.add(instance);
        }

        StringToWordVector filter = new StringToWordVector();
        filter.setTFTransform(true);
        filter.setIDFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);
        filter.setWordsToKeep(1000);
        filter.setInputFormat(dataset);

        filteredData = Filter.useFilter(dataset, filter);

        classifier = new RandomForest();
        classifier.buildClassifier(filteredData);
    }

    @FXML
    public void onChooseImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Article Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imageUrl = file.toURI().toString();
            showAlert("Image Selected", "Image selected: " + file.getName());
        }
    }

    @FXML
    public void onSavebuttonclick() {
        String id = articleid.getText();
        String title = articletitle.getText();
        String content = articlecontent.getText();

        if (id.isEmpty() || title.isEmpty() || content.isEmpty() || imageUrl == null) {
            showAlert("Validation Error", "Please fill all fields and select an image.");
            return;
        }

        try {
            String category = analyzeCategory(content);
            saveToDatabase(id, title, content, imageUrl, category);
        } catch (Exception e) {
            showAlert("Error", "Failed to process article: " + e.getMessage());
        }
    }

    private String analyzeCategory(String content) throws Exception {
        // Create a new instance for the input article
        Instance newInstance = new DenseInstance(2);
        newInstance.setDataset(dataset);
        newInstance.setValue((Attribute) dataset.attribute(0), content);

        // Add to test set
        Instances testSet = new Instances(dataset, 0);
        testSet.add(newInstance);

        // Apply the StringToWordVector filter to the test set
        StringToWordVector filter = new StringToWordVector();
        filter.setTFTransform(true);
        filter.setIDFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setWordsToKeep(1000);
        filter.setInputFormat(dataset);

        Instances filteredTestSet = Filter.useFilter(testSet, filter);

        // Classify the instance
        double predictedIndex = classifier.classifyInstance(filteredTestSet.instance(0));
        String category = dataset.classAttribute().value((int) predictedIndex);

        // Debug logs
        System.out.println("Content: " + content);
        System.out.println("Predicted Index: " + predictedIndex);
        System.out.println("Predicted Category: " + category);

        return category;
    }


    private void saveToDatabase(String id, String title, String content, String imageUrl, String category) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO articles (article_id, category, content, imageUrl, title, user_id) VALUES (?, ?, ?, ?, ?, NULL)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(id));
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, content);
            preparedStatement.setString(4, imageUrl);
            preparedStatement.setString(5, title);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                showAlert("Success", "Article added successfully!");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save the article: " + e.getMessage());
        }
    }

    private void clearFields() {
        articleid.clear();
        articletitle.clear();
        articlecontent.clear();
        imageUrl = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void onBackbuttonclick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
            Stage stage = (Stage) articleid.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to navigate back: " + e.getMessage());
        }
    }

    @FXML
    public void onExitbuttonclick() {
        System.exit(0);
    }
}
