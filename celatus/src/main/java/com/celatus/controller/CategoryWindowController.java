package com.celatus.controller;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordsDatabase;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyEvent;

public class CategoryWindowController extends DialogWindowController {
    
    // region =====Variables

    @FXML
    private Label label02;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    // endregion

    // region =====Event Methods=====

    @FXML
    private void checkName(KeyEvent event) {
        int maxLength = 50;
        String currentName = nameTextField.getText();
        if (currentName.length() >= maxLength) {
            nameTextField.setText(currentName.substring(0, maxLength));
            nameTextField.positionCaret(maxLength);
        }
    }

    @FXML
    private void checkDescription(KeyEvent event) {
        int maxLength = 250;
        // We don't allow descriptions over 250 characters long
        String currentText = descriptionTextArea.getText();
        if (currentText.length() >= maxLength) {
            descriptionTextArea.setText(currentText.substring(0, maxLength));
            descriptionTextArea.positionCaret(maxLength);
        }
    }  
    
    @FXML
    private void saveCategory() {
        String name = nameTextField.getText();
        if (name == null || name.isEmpty()) {
            label02.setText("This field is required");
            return;
        }

        String description = descriptionTextArea.getText();
        if (description.isEmpty()) {
            description = null;
        }
        Category category = new Category(name, description, null);
        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
        try {
            passwordsDatabase.addCategory(category);
        } catch (IllegalArgumentException ex) {
            label02.setText("This category already exists");
            return;
        }
        // Update the list view in the main window
        Scene appScene = owner.getScene();
        @SuppressWarnings("unchecked") ListView<String> categoriesList = (ListView<String>) appScene.lookup("#categoriesList");
        FXMLUtils.addToListView(categoriesList, name);
        closeDialog();
    }
    
    // endregion

}
