package com.celatus.controller;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordsDatabase;
import com.celatus.util.FXMLUtils;

import java.util.Map;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang3.StringUtils;

public class CategoryWindowController extends DialogWindowController {
    
    // region =====Variables

    @FXML
    private Label title;
    @FXML
    private Label label02;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    private Category inputCategory;

    // endregion

    // region =====Getters and Setters=====

    public Category getInputCategory() {
        return inputCategory;
    }

    public void setInputCategory(Category category) {
        inputCategory = category;
    }

    // endregion

    // region =====Window Methods=====

    public void initialize() {
        super.initialize();
        Platform.runLater(() -> {
            fillFields();
        });
    }

    public void fillFields() {
        if (inputCategory != null) {
            nameTextField.setText(inputCategory.getName());
            
            if (inputCategory.getDescription() != null) {
                descriptionTextArea.setText(inputCategory.getDescription());
            }  
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

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
        Scene appScene = owner.getScene();
        @SuppressWarnings("unchecked") ListView<String> categoriesList = (ListView<String>) appScene.lookup("#categoriesList");
        AnchorPane descriptionPane = (AnchorPane) appScene.lookup("#descriptionPane");

        String name = nameTextField.getText();
        String description = descriptionTextArea.getText();
        if (StringUtils.isBlank(name)) {
            label02.setText("This field is required");
            return;
        }

        Category category = new Category(name, description, null);
        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();

        if (inputCategory == null) {
            try {
                // Updating the passwords database
                passwordsDatabase.addCategory(category);
                // Updating the main window categories list
                FXMLUtils.addToListView(categoriesList, name);
            } catch (IllegalArgumentException ex) {
                label02.setText("This category already exists");
                return;
            } catch (Exception ex) {
                App.error(window, "An error occured: " + ex, logger, PopupMode.OK);
            }
        } else {
            String oldName = inputCategory.getName();
            inputCategory.setName(name);
            inputCategory.setDescription(description);
            // Updating the main window categories list and category description
            FXMLUtils.updateListView(categoriesList, oldName, name);
            if (StringUtils.isNotBlank(description)) {
                MainWindowController controller = (MainWindowController) App.getController();
                controller.showDescription(description);
            } else {
                descriptionPane.setVisible(false);
            }
        }
        
        closeDialog();
    }
    
    // endregion

}
