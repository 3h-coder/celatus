package com.celatus.controller;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordEntry;
import com.celatus.PasswordsDatabase;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller of the window used to create and edit password entries
 */
public class PasswordWindowController extends AlertWindowController {

    // region =====Variables=====

    @FXML
    private Label title;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField urlField;
    @FXML
    private TextField identifierField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField revealedPasswordField;
    @FXML
    private TextArea passwordNotes;
    @FXML
    private Label createdLabel;
    @FXML
    private Label lastEditedLabel;
    @FXML
    private Label checkNameLabel;
    @FXML
    private Label checkIdentifierLabel;
    @FXML
    private Label checkEmailLabel;
    @FXML
    private Label checkPasswordLabel;
    @FXML
    private Button viewButton;

    private Category category;
    private PasswordEntry inputPwdEntry;
    private String password;

    // endregion

    // region =====Getters and Setters=====

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public PasswordEntry getInputPwdEntry() {
        return inputPwdEntry;
    }

    public void setInputPwdEntry(PasswordEntry pwdEntry) {
        this.inputPwdEntry = pwdEntry;
    }
    
    // endregion

    // region =====Window Methods=====

    public void initialize() {
        super.initialize();
        Platform.runLater(() -> {
            fillFields();
            // Dynamic password update
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                password = newValue;
                revealedPasswordField.setText(password);
            });
            revealedPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
                password = newValue;
                passwordField.setText(password);
            });
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * Fills the window fields in case we're editing an existing password entry
     */
    public void fillFields() {
        if (inputPwdEntry == null) {
            return;
        }

        nameTextField.setText(inputPwdEntry.getName());
        urlField.setText(inputPwdEntry.getUrl());
        identifierField.setText(inputPwdEntry.getIdentifier());
        emailField.setText(inputPwdEntry.getEmail());
        this.password = inputPwdEntry.getPassword();
        passwordField.setText(inputPwdEntry.getPassword());
        revealedPasswordField.setText(inputPwdEntry.getPassword());
        passwordNotes.setText(inputPwdEntry.getNotes());
        createdLabel.setText("Created : " + CustomDateUtils.prettyDate(inputPwdEntry.getCreationDate()));
        lastEditedLabel.setText("Last edited : " + CustomDateUtils.prettyDate(inputPwdEntry.getLastEditDate()));
    }

    // endregion

    // region =====Event Methods=====

    /**
     * Saves our password entry into the database, or updates it if it's an existing one
     */
    @FXML
    private void savePassword() {

        // We first check that none of the required fiels is blank
        if (!requiredFieldsNotBlank()) {
            return;
        }

        String name = nameTextField.getText();
        String identifier = identifierField.getText();
        String url = urlField.getText();
        String email = emailField.getText();
        String notes = passwordNotes.getText();

        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
        this.category = passwordsDatabase.getCategory(this.category.getName());
        MainWindowController controller = (MainWindowController) App.getController();

        // Saving the new password entry
        if (inputPwdEntry == null) {
            PasswordEntry pwdEntry = new PasswordEntry(name, url, notes, identifier, email, this.password);
            this.category.addPasswordEntry(pwdEntry);
            logger.info("Adding the following password entry to the " + this.category.getName() + "category : " + pwdEntry);

            closeDialog();
            summonPopup(App.getWindow(), "Password entry created");
        // Updating the exisiting password entry
        } else if (changesDetected()) {

            inputPwdEntry.setName(name);
            inputPwdEntry.setIdentifier(identifier);
            inputPwdEntry.setUrl(url);
            inputPwdEntry.setPassword(this.password);
            inputPwdEntry.setEmail(email);
            inputPwdEntry.setNotes(notes);
            inputPwdEntry.setLastEditDate(LocalDateTime.now());

            closeDialog();
            summonPopup(App.getWindow(), "Password entry updated");
        }
        closeDialog();
        // Refreshing the password entry view in the main window
        controller.displayPasswords(category);
    }

    /**
     * Separate method to check that all of our required fields are not blank
     * @return <b>true</b> if no field is blank, <b>false</b> otherwise
     */
    @FXML
    private boolean requiredFieldsNotBlank() {

        boolean allOk = true;

        checkNameLabel.setText(null);
        String name = nameTextField.getText();
        if (StringUtils.isBlank(name)) {
            checkNameLabel.setText("This field is required");
            allOk = false;
        }

        checkIdentifierLabel.setText(null);
        String identifier = identifierField.getText();
        if (StringUtils.isBlank(identifier)) {
            checkIdentifierLabel.setText("This field is required");
            allOk = false;
        }

        checkPasswordLabel.setText(null);
        if (StringUtils.isBlank(this.password)) {
            checkPasswordLabel.setText("This field is requied");
            allOk = false;
        }

        return allOk;
    }

    /**
     * Detects whether or nto changes have been made to the password entry (in case of editing)
     * @return
     */
    private boolean changesDetected() {

        String name = nameTextField.getText();
        String identifier = identifierField.getText();
        String url = urlField.getText();
        String email = emailField.getText();
        String notes = passwordNotes.getText();

        if (!StringUtils.equals(inputPwdEntry.getName(), name)) {
            return true;
        }
        if (!StringUtils.equals(inputPwdEntry.getIdentifier(), identifier)) {
            return true;
        }
        if (!StringUtils.equals(inputPwdEntry.getUrl(), url)) {
            return true;
        }
        if (!StringUtils.equals(inputPwdEntry.getEmail(), email)) {
            return true;
        }
        if (!StringUtils.equals(inputPwdEntry.getNotes(), notes)) {
            return true;
        }

        return false;
    }

    // endregion
    
}
