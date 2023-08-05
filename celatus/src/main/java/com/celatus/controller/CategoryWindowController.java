package com.celatus.controller;

import com.celatus.App;
import com.celatus.Category;

import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

public class CategoryWindowController extends DialogWindowController {
    
    // region =====Variables

    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    // endregion

    // region =====Event Methods=====

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
    
    // endregion
}
