package com.celatus.controller;

import com.celatus.App;
import com.celatus.AuthHandler;
import com.celatus.DatabaseHandler;
import com.celatus.PasswordsDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller of our entry window, after the master password has been set by the user
 */
public class EntryWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private Button closeButton;
    @FXML
    private Button entryButton;
    @FXML
    protected Button viewButton;
    @FXML
    private Label label01;
    @FXML
    private Label label02;
    @FXML
    private PasswordField pwdField;
    @FXML 
    private TextField revealedPwdField;

    private String password;

    // endregion

    // region =====Window Methods=====

    @FXML
    public void warning(String message) {
        label02.setText(message);
        label02.setVisible(true);
    } 

    @FXML
    private void setPasswordValue() {
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
        } else {
            password = revealedPwdField.getText();
        }
    }

    /**
     * Method used to enter the App
     */
    @FXML
    private void submitPassword() {
        setPasswordValue();
        
        if (AuthHandler.correctPassword(password)) {
            AuthHandler.enterApp();
            switchToMainWindow();
        } else {
            warning("Incorrect master password");
        }     
    }

    // endregion

    // region =====Event Methods=====

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

    // endregion
}
