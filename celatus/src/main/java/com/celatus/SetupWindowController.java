package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.ContextMenu;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetupWindowController extends EntryWindowController {

    private static final Logger logger = LogManager.getLogger(SetupWindowController.class.getName());

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

    private String password2;
    // endregion

    // region ===== Main Methods=====

    @FXML
    private void initialize() {
        pwdField2.setContextMenu(createEmptyContextMenu());
    }

    @FXML
    private void submitPassPhrases() {
        // Getting the master password
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

        if(validEqualPasswords(password, password2)) {
            // System.out.println("The password is: " + password);
            // System.out.println("The key from it is: " + CryptoUtils.generateAESKey(password));
            try {
                App.setKey(CryptoUtils.generateAESKey(password));
                App.launchDialogWindow(getCurrentWindow(), "holala");
            } catch (Exception ex) {
                App.error(getCurrentWindow(),"An unexpected error occured when trying to summon the main window : " + ex);
                close();
            }
            
        }   
    }

    // endregion

    // region =====Secondary Methods=====

    private ContextMenu createEmptyContextMenu() {
        return new ContextMenu();
    }

    @FXML
    private void warning(String message) {
        label04.setText(message);
        label04.setVisible(true);
    } 

    @FXML
    private boolean validEqualPasswords(String password1, String password2) {

        boolean valid = true;
        // Check if both master passwords match
        if (!password1.equals(password2)) {
            warning("Both passwords do not match");
            System.out.println(password1);
            System.out.println(password2);
            return false;
        } else {
            label04.setVisible(false);
            // Check if the master password's length is sufficient
            if (password1.length() < 30) {
                warning("The master password must be at least 30 characters long");
                return false;
            // Check if no whitespace at the beginning
            } else if (password1.startsWith(" ")) {
                warning("White spaces are not allowed at the start of the master password");
                return false;
            // Check if no whitespace at the end
            } else if (password1.endsWith(" ")) {
                warning("White spaces are not allowed at the end of the master password");
                return false;
            }
        }
        return valid;
    }

    @FXML
    private void goToPwdField2() {
        pwdField2.requestFocus();
    }

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
