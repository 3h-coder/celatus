package com.celatus.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PopupWindowController extends DialogWindowController {
    
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

    // endregion
}
