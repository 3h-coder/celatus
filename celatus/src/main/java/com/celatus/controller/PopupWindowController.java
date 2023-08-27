package com.celatus.controller;

import java.io.IOException;

import com.celatus.App;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PopupWindowController extends DialogWindowController {
    
    // region =====Variables=====

    @FXML
    private ImageView popupIcon;
    @FXML
    private Label mainMessage;
    @FXML
    private Button okButton;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    
    
    private PopupMode mode;

    // endregion

    // region =====Window Methods=====

    @FXML
    public void setMessage(String message) {
        mainMessage.setText(message);
    }

    @FXML
    public void setIcon(String imagePath) throws IOException {
        Image image = new Image(App.class.getResourceAsStream("images/" + imagePath));
        popupIcon.setImage(image);
    }

    @FXML
    public void setMode(PopupMode mode) {
        if (mode == PopupMode.OK) {
            okButton.setVisible(true);
            yesButton.setVisible(false);
            noButton.setVisible(false);
        } else if (mode == PopupMode.YES_AND_NO) {
            okButton.setVisible(false);
            yesButton.setVisible(true);
            noButton.setVisible(true);
        }
    }
    // endregion


    // region =====Event Methods=====

    public void sendOKSignal() {
        logger.info("Sending the signal: OK");
        App.addTempVariable("ok_signal", true);
        closeDialog();
    }

    public void sendYesSignal() {
        logger.info("Sending the signal: YES");
        App.addTempVariable("yes_signal", true);
        closeDialog();
    }

    public void sendNoSignal() {
        logger.info("Sending the signal: NO");
        App.addTempVariable("no_signal", true);
        closeDialog();
    }

    // endregion
}