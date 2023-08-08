package com.celatus.controller;

import com.celatus.App;
import com.celatus.AuthHandler;
import com.celatus.PasswordsDatabase;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.ContextMenu;

public class SetupWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private PasswordField pwdField;
    @FXML
    private PasswordField pwdField2;
    @FXML
    private Button viewButton;
    @FXML
    private Button viewButton2;
    @FXML
    private Button enterButton;
    @FXML 
    private TextField revealedPwdField;
    @FXML 
    private TextField revealedPwdField2;
    @FXML
    private Label label01;
    @FXML
    private Label label02;
    @FXML
    private Label label03;
    @FXML
    private Label label04;

    private String password;
    private String password2;

    // endregion

    // region =====Window Methods=====

    @Override 
    public void initialize() {
        super.initialize();
        pwdField2.setContextMenu(new ContextMenu());
    }

    @FXML
    public void warning(String message) {
        label04.setText(message);
        label04.setVisible(true);
    } 

    @FXML
    private void setPasswordsValue() {
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
        } else {
            password = revealedPwdField.getText();
        }
        if ("View".equals(viewButton2.getText())) {
            password2 = pwdField2.getText();
        } else {
            password2 = revealedPwdField2.getText();
        }
    }

    @FXML
    private void submitMasterPasswords() {
        setPasswordsValue();

        Map<Boolean, String> passwordsCheckInfo = AuthHandler.checkPasswords(password, password2);
        boolean validPasswords = (boolean)passwordsCheckInfo.keySet().toArray()[0];
        if (!validPasswords) {
            String invalidPasswordsMessage = (String)passwordsCheckInfo.values().toArray()[0];
            warning(invalidPasswordsMessage);
        } else {
            try {
                AuthHandler.setAppKey(password);
                App.setPasswordsDatabase(PasswordsDatabase.generateDefault());
                switchWindow("mainWindow");
            } catch (Exception ex) {
                App.error(window,"An unexpected error occured when trying to summon the main window : " + ex, logger, PopupMode.OK);
                close();
            }
        }
    }

    @FXML
    private void goToPwdField2() {
        if ("View".equals(viewButton2.getText())) {
            pwdField2.requestFocus();         
        } else {
            revealedPwdField2.requestFocus();
        }    
    }

    // endregion

    // region =====Event Methods=====

    @FXML
    private void pwdFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            goToPwdField2();
        }
    }

    @FXML
    private void pwdField2KeyPressed(KeyEvent event) {
        // Prevent copy pasting into this field
        if (event.isControlDown() && event.getCode() == KeyCode.V) {
            warning("Copy pasting is not allowed in this field");
            pwdField2.setText(password2);
            pwdField2.positionCaret(password2.length());
        } else {
            password2 = pwdField2.getText();
        }
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
    private void viewButton2Clicked() {
        if ("View".equals(viewButton2.getText())) {
            viewButton2.setText("Hide");
            pwdField2.setVisible(false);
            revealedPwdField2.setVisible(true);
            revealedPwdField2.setText(password2);
        } else {
            viewButton2.setText("View");
            revealedPwdField2.setVisible(false);
            pwdField2.setVisible(true);
            pwdField2.setText(password2);
        } 
    }

    // endregion

    
}
