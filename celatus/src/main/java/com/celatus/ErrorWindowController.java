package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorWindowController extends BaseWindowController {
    
    @FXML
    public Label errorMessage;

    @FXML
    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    @FXML
    public void close() {
         // Get the stage (window) associated with the button
        Stage stage = (Stage) errorMessage.getScene().getWindow();
        // Close the stage
        stage.close();
    }
}
