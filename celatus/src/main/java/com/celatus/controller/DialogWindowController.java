package com.celatus.controller;

import com.celatus.App;
import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;



public class DialogWindowController extends BaseWindowController {

    // region =====Event Methods=====

    @FXML
    public void closeDialog() {
        FXMLUtils.removeDarkOverlay(App.getScene());
        window.close();
    }

    // endregion
}
