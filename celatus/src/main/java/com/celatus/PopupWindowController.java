package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopupWindowController extends BaseWindowController {
    
    // region =====Variables=====

    @FXML
    public Label mainMessage;

    // endregion

    // region =====Window Methods=====

    @FXML
    public void setMessage(String message) {
        mainMessage.setText(message);
    }

    // endregion


    // region =====Event Methods=====

    @FXML
    public void close() {
        // Close it
        window.close();
    }

    // endregion
}
