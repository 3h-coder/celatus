package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

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
    private void pwdField2KeyPressed(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.V) {
            thirdLabel.setText("Copy pasting is not allowed in this field");
            thirdLabel.setVisible(true);
            pwdField2.setText(password2);
            pwdField2.positionCaret(password2.length());
        } else {
            password2 = pwdField2.getText();
        }
    }

    @FXML
    private void handlePaste() {

    }

    @FXML
    private boolean validPasswords(String password1, String password2) {

        boolean valid = true;
        // Check if both pass phrases match
        if (!password1.equals(password2)) {
            thirdLabel.setText("The pass phrases do not match");
            thirdLabel.setVisible(true);
            return false;
        } else {
            thirdLabel.setVisible(false);
            // Check if the pass phrase's length is sufficient
            if (password1.length() < 30) {
                thirdLabel.setText("The pass phrase must be at least 30 characters long");
                thirdLabel.setVisible(true);
                return false;
            // Check if no whitespace at the beginning
            } else if (password1.startsWith(" ")) {
                thirdLabel.setText("White spaces are not allowed at the start of the pass phrase");
                thirdLabel.setVisible(true);
                return false;
            // Check if no whitespace at the end
            } else if (password1.endsWith(" ")) {
                thirdLabel.setText("White spaces are not allowed at the end of the pass phrase");
                thirdLabel.setVisible(true);
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

    // endregion
}
