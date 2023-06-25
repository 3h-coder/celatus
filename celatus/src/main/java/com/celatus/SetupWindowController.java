package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SetupWindowController extends EntryWindowController {

    @FXML
    private PasswordField pwdField;
    
    @FXML
    private PasswordField pwdField2;

    @FXML 
    private TextField revealedPwdField;

    @FXML
    private Label thirdLabel;

    private String password2;

    @FXML
    private void goToPwdField2() {
        pwdField2.requestFocus();
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
        if (!password.equals(password2)) {
            thirdLabel.setVisible(true);
        } else {
            thirdLabel.setVisible(false);
        }
    }
}
