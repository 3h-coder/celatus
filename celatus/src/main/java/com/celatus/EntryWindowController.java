package com.celatus;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class EntryWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private Button closeButton;

    @FXML
    private Button entryButton;

    @FXML
    protected Button viewButton;

    @FXML
    private Label mainLabel;
    
    @FXML
    private PasswordField pwdField;

    @FXML 
    private TextField revealedPwdField;

    protected String password;

    // endregion

    // region =====Methods=====

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
        // Getting the pass phrase
        if ("View".equals(viewButton.getText())) {
            password = pwdField.getText();
        } else {
            password = revealedPwdField.getText();
        }
        // TODO : check pass phrase
        // System.out.println("Entered password is : " + password);
    }

    // endregion
}
