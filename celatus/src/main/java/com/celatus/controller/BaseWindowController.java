package com.celatus.controller;

import com.celatus.App;
import com.celatus.util.FXMLUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.fxml.FXML;
import javafx.stage.Screen;
import javafx.scene.Scene;
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
    protected AnchorPane rootPane;
    @FXML
    protected Button minimizeButton;
    @FXML
    protected Button closeButton;
    @FXML
    protected Scene scene;
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
            scene = window.getScene();
        });
    }

    @FXML
    public Stage getCurrentWindow() {
        return (Stage) rootPane.getScene().getWindow();
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
            App.error(window, "An unexpected error occured when trying to open " + fxml + ": " + ex, logger, PopupMode.OK);
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

    public boolean isMaximized() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double minX = primaryScreenBounds.getMinX();
        double minY = primaryScreenBounds.getMinY();
        double screenWidth = primaryScreenBounds.getWidth();
        double screenHeight = primaryScreenBounds.getHeight();
        return window.getWidth() == screenWidth && window.getHeight() == screenHeight && window.getX() == minX && window.getY() == minY;
    }

    public void maximize() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double minX = primaryScreenBounds.getMinX();
        double minY = primaryScreenBounds.getMinY();
        double screenWidth = primaryScreenBounds.getWidth();
        double screenHeight = primaryScreenBounds.getHeight();

        // If the window is already maximized, we set it back to default 
        if (window.getWidth() == screenWidth && window.getHeight() == screenHeight && window.getX() == minX && window.getY() == minY) {
            window.setWidth(900);
            window.setHeight(600);
            window.centerOnScreen();
        } else {
            window.setWidth(screenWidth);
            window.setHeight(screenHeight);
            window.setX(minX);
            window.setY(minY);
        }
    }
    
    // endregion
}
