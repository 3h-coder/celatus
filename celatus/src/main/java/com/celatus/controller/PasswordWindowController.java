package com.celatus.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordEntry;
import com.celatus.PasswordsDatabase;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.PasswordField;

public class PasswordWindowController extends PopupWindowController {

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

    @FXML
    private void savePassword() {
        Scene appScene = owner.getScene();
        @SuppressWarnings("unchecked") TableView<PasswordEntry> passwordsTable = (TableView<PasswordEntry>) appScene.lookup("#passwordsTable");

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

        if (inputPwdEntry == null) {
            PasswordEntry pwdEntry = new PasswordEntry(name, url, notes, identifier, email, this.password);
            this.category.addPasswordEntry(pwdEntry);
            logger.info("Adding the following password entry to the " + this.category.getName() + "category : " + pwdEntry);
        } else {
            inputPwdEntry.setName(name);
            inputPwdEntry.setIdentifier(identifier);
            inputPwdEntry.setPassword(this.password);
            inputPwdEntry.setEmail(email);
            inputPwdEntry.setNotes(notes);
            inputPwdEntry.setLastEditDate(LocalDateTime.now());
        }

        controller.displayPasswords(category);
        closeDialog();
    }

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

    // endregion
    
}