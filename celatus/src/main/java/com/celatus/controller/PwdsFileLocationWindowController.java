package com.celatus.controller;

import java.io.File;

import com.celatus.App;
import com.celatus.handler.DatabaseHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PwdsFileLocationWindowController extends DialogWindowController {

    // region =====Variables=====

    @FXML
    private TextField pwdsFileLocationTextField;
    @FXML
    private Label errorLabel;

    // endregion

    // region =====Window methods=====

    @Override
    public void initialize() {
        super.initialize();
        // Initialize the text field for the password file location
        pwdsFileLocationTextField.setText(DatabaseHandler.getDBFolderPath());
    }

    // endregion

    // region =====Event methods=====

    @FXML
    private void openFolderDialogForPwdLocation() {
        OpenPwdsFileLocationDialog(pwdsFileLocationTextField, window);
    }

    @FXML
    private void saveButtonClicked() {
        try {
            DatabaseHandler.setDBFolderPath(pwdsFileLocationTextField.getText());
            closeDialog();
            summonNotificationPopup(App.getWindow(), "Successfully saved the new password file location.");
        } catch (Exception e) {
            errorLabel.setText("Invalid path, please select another location.");
        }
    }

    // endregion

    // region =====Shared utility methods=====

    public static void OpenPwdsFileLocationDialog(TextField pwdFileLocationField, Stage window) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Password File Location");
        // Set initial directory from the current text field value
        String currentLocation = pwdFileLocationField.getText();
        if (currentLocation != null && !currentLocation.isBlank()) {
            var initialDir = new File(currentLocation);
            if (initialDir.exists() && initialDir.isDirectory()) {
                directoryChooser.setInitialDirectory(initialDir);
            }
        }
        File selectedDirectory = directoryChooser.showDialog(window);
        if (selectedDirectory != null) {
            pwdFileLocationField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    // endregion
}
