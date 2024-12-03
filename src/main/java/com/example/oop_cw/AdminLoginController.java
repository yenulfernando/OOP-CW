package com.example.oop_cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;



public class AdminLoginController {
    @FXML
    public TextField usernametext;
    @FXML
    public PasswordField passwordtext;
    @FXML
    public Label emptylabel;


    @FXML
    protected void onLoginbuttonclick(ActionEvent event)throws IOException {
        String username = usernametext.getText();
        String password = passwordtext.getText();
        if (username.isEmpty()) {
            emptylabel.setText("(please enter your valid username!)");
            JOptionPane.showMessageDialog(null, "please enter your valid username!.");
        }
        else if (password.isEmpty()) {
            emptylabel.setText("(please enter your valid password!)");
            JOptionPane.showMessageDialog(null, "please enter your valid password!.");
        }
        else if (username.equals("yenul") && password.equals("yenul")) {
            emptylabel.setText("logging successful...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            emptylabel.setText("logging failed...");
            JOptionPane.showMessageDialog(null, "logging failed...");
        }



    }
    @FXML
    protected void onBackbuttonclick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Start.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

}