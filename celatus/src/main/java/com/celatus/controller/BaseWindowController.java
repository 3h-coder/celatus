package com.celatus.controller;

import com.celatus.App;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    protected final Logger logger = LogManager.getLogger(this.getClass().toString().replace("class ", ""));

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Button minimizeButton;
    @FXML
    protected Button closeButton;
    @FXML
    protected Stage window;

    private static double xOffset = 0;
    private static double yOffset = 0;

    // ====================

    // region =====Window Methods=====

    public void initialize() {
        // We have to make sure the scene is fully initialized to properly set up our variables
        Platform.runLater(() -> {
            window = getCurrentWindow();
        });
    }

    @FXML
    public Stage getCurrentWindow() {
        return (Stage) pane.getScene().getWindow();
    }
    

    /**
     * Switches window with the provided one. In other terms, closes the current window to open the specified one.
     * @param fxml
     */
    public void switchWindow(String fxml) {
        try {
            App.launchWindow(fxml);
            window.close();
        } catch (Exception ex) {
            App.error(window, "An unexpected error occured when trying to open " + fxml + ": " + ex, logger);
            close();
        }
    }

    // endregion
    
    // region =====Event Methods=====

    @FXML
    public void windowKeyPressed(KeyEvent event) {
        // Quit program on alt + F4
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

    public void close() {
        Platform.exit();
    }

    public void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
    
    // endregion
}
