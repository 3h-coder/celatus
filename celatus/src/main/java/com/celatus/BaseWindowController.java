package com.celatus;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class BaseWindowController {

    // region =====Variables=====

    @FXML
    protected Button minimizeButton;

    @FXML
    protected Button closeButton;

    protected Stage window;

    // ====================

    // region =====Methods=====

    @FXML
    public Stage getCurrentWindow() {
        return (Stage) closeButton.getScene().getWindow();
    }
    
    @FXML
    public void close() {
        Platform.exit();
    }

    @FXML
    public void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        // Minimize the stage
        stage.setIconified(true);
    }

    @FXML
    public void windowKeyPressed(KeyEvent event) {
        if (event.isAltDown() && event.getCode() == KeyCode.F4) {
            close();
        }
    }

    // endregion
}
