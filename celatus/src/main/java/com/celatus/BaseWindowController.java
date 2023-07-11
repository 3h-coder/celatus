package com.celatus;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;

public class BaseWindowController {

    // region =====Variables=====

    @FXML
    protected AnchorPane pane;

    @FXML
    protected Button minimizeButton;

    @FXML
    protected Button closeButton;

    private static double xOffset = 0;
    private static double yOffset = 0;

    // ====================

    // region =====Methods=====

    @FXML
    public Stage getCurrentWindow() {
        return (Stage) pane.getScene().getWindow();
    }
    
    @FXML
    public void close() {
        Platform.exit();
    }

    @FXML
    public void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void windowKeyPressed(KeyEvent event) {
        if (event.isAltDown() && event.getCode() == KeyCode.F4) {
            close();
        }
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        Stage window = getCurrentWindow();
        xOffset = window.getX() - event.getScreenX();
        yOffset = window.getY() - event.getScreenY();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        Stage window = getCurrentWindow();
        window.setX(event.getScreenX() + xOffset);
        window.setY(event.getScreenY() + yOffset);
    }

    /**
     * Switches window with the provided one. In other terms, closes the current window to open the specified one.
     * @param fxml
     */
    public void switchWindow(String fxml) {
        Stage window = getCurrentWindow();
        try {
            App.launchWindow(fxml);
            window.close();
        } catch (Exception ex) {
            App.error(window, "An unexpected error occured when trying to open " + fxml + ": " + ex);
            close();
        }
    }

    // endregion
}
