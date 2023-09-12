package com.celatus.controller;

import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/** 
* Base controller for all of our dialog window coontrollers
*/
public class DialogWindowController extends BaseWindowController {

    // region =====Variables=====

    public Stage owner;

    // endregion

    // region ======Window Methods=====

    public void initialize() {
        super.initialize();
        Platform.runLater(() -> {
            owner = (Stage) window.getOwner();
            if (owner != null) {
                FXMLUtils.addDarkOverlay(owner.getScene());
            }  
        });
    }

    // endregion

    // region =====Event Methods=====

    @Override
    public void windowKeyPressed(KeyEvent event) {
        super.windowKeyPressed(event);
        KeyCode eventCode = event.getCode();
        if (eventCode == KeyCode.ESCAPE) {
            closeDialog();
        }
    }
    
    @FXML
    public void closeDialog() {
        if (owner != null) {
            FXMLUtils.removeDarkOverlay(owner.getScene());
        }
        window.close();
    }

    // endregion
}
