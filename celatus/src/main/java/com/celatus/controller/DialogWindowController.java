package com.celatus.controller;

import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
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
            FXMLUtils.addDarkOverlay(owner.getScene());
        });
    }

    // endregion

    // region =====Event Methods=====

    @FXML
    public void closeDialog() {
        FXMLUtils.removeDarkOverlay(owner.getScene());
        window.close();
    }

    // endregion
}
