package com.celatus;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class EntryWindowController {

    @FXML
    private Button closeButton;

    @FXML
    private Button entryButton;

    @FXML
    private Button viewButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Label mainLabel;
    
    @FXML
    private PasswordField pwdField;

    @FXML 
    private TextField revealedPwdField;

    private String password;

    @FXML
    private void onPasswordKeyTyped() {
        System.out.println("test");
    }

    @FXML
    private void viewButtonClicked() {
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
            viewButton.setText("Hide");
            pwdField.setVisible(false);
            revealedPwdField.setVisible(true);
            revealedPwdField.setText(password);
        } else {
            password = revealedPwdField.getText();
            viewButton.setText("View");
            revealedPwdField.setVisible(false);
            pwdField.setVisible(true);
            pwdField.setText(password);
        }    
    }

    @FXML
    private void submitPassword() {
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
        } else {
            password = revealedPwdField.getText();
        }
        System.out.println("Entered password is : " + password);
    }

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        // Minimize the stage
        stage.setIconified(true);
    }
}
