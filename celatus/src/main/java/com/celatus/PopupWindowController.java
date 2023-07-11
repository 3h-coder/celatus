package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopupWindowController extends BaseWindowController {
    
    @FXML
    public Label mainMessage;

    @FXML
    public void setErrorMessage(String message) {
        mainMessage.setText(message);
    }

    @FXML
    public void close() {
        // Get the current window
        Stage window = getCurrentWindow();
        // Close it
        window.close();
    }
}
