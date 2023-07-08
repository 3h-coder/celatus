package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PopupWindowController extends BaseWindowController {
    
    @FXML
    public Label errorMessage;

    @FXML
    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    @FXML
    public void close() {
        // Get the current window
        window = getCurrentWindow();
        // Close it
        window.close();
    }
}
