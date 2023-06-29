package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.ContextMenu;

public class SetupWindowController extends EntryWindowController {

    // region =====Variables=====

    @FXML
    private PasswordField pwdField;
    
    @FXML
    private PasswordField pwdField2;

    @FXML 
    private TextField revealedPwdField;

    @FXML
    private Label thirdLabel;

    private String password2;

    // endregion

    // region =====Methods=====

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
    private void warning(String message) {
        thirdLabel.setText(message);
        thirdLabel.setVisible(true);
    } 

    private ContextMenu createEmptyContextMenu() {
        return new ContextMenu();
    }

    @FXML
    private void pwdField2KeyPressed(KeyEvent event) {
        // Prevent copy pasting into this field
        if (event.isControlDown() && event.getCode() == KeyCode.V) {
            warning("Copy pasting is not allowed in this field");
            /*pwdField2.setText(password2);
            pwdField2.positionCaret(password2.length());*/
        } else {
            password2 = pwdField2.getText();
        }
    }

    @FXML
    private boolean validPasswords(String password1, String password2) {

        boolean valid = true;
        // Check if both pass phrases match
        if (!password1.equals(password2)) {
            warning("The pass phrases do not match");
            return false;
        } else {
            thirdLabel.setVisible(false);
            // Check if the pass phrase's length is sufficient
            if (password1.length() < 30) {
                warning("The pass phrase must be at least 30 characters long");
                return false;
            // Check if no whitespace at the beginning
            } else if (password1.startsWith(" ")) {
                warning("White spaces are not allowed at the start of the pass phrase");
                return false;
            // Check if no whitespace at the end
            } else if (password1.endsWith(" ")) {
                warning("White spaces are not allowed at the end of the pass phrase");
                return false;
            }
        }
        return valid;
    }

    @FXML
    private void submitPassword() {
        // Getting the pass phrase
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
        } else {
            password = revealedPwdField.getText();
        }
        password2 = pwdField2.getText();

        if(validPasswords(password, password2)) {
            // System.out.println("Yay both pass phrases are valid and the same!");
        }

        
    }

    @FXML
    private void initialize() {
        pwdField2.setContextMenu(createEmptyContextMenu());
    }
    // endregion
}
