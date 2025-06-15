package com.celatus.controller;

import java.io.IOException;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.enums.AppTempVariable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.SVGPath;

/**
 * Controller for our alert windows, which can be error, warning, or info
 * windows
 */
public class AlertWindowController extends DialogWindowController {

  // region =====Variables=====

  @FXML
  private SVGPath popupIcon;
  @FXML
  private Label mainMessage;
  @FXML
  private Button okButton;
  @FXML
  private Button yesButton;
  @FXML
  private Button noButton;

  // endregion

  // region =====Window Methods=====

  @FXML
  public void setMessage(String message) {
    mainMessage.setText(message);
  }

  @FXML
  public void setErrorIcon() throws IOException {
    final String CIRCLE_XMARK_CONTENT = "M256 48a208 208 0 1 1 0 416 208 208 0 1 1 0-416zm0 464A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM175 175c-9.4 9.4-9.4 24.6 0 33.9l47 47-47 47c-9.4 9.4-9.4 24.6 0 33.9s24.6 9.4 33.9 0l47-47 47 47c9.4 9.4 24.6 9.4 33.9 0s9.4-24.6 0-33.9l-47-47 47-47c9.4-9.4 9.4-24.6 0-33.9s-24.6-9.4-33.9 0l-47 47-47-47c-9.4-9.4-24.6-9.4-33.9 0z";
    popupIcon.setContent(CIRCLE_XMARK_CONTENT);
  }

  /**
   * Sets the window according to the specified mode
   *
   * @param mode : For example OK, or YES_AND_NO
   */
  @FXML
  public void setMode(AlertMode mode) {
    if (mode == AlertMode.OK) {
      okButton.setVisible(true);
      yesButton.setVisible(false);
      noButton.setVisible(false);
    } else if (mode == AlertMode.YES_AND_NO) {
      okButton.setVisible(false);
      yesButton.setVisible(true);
      noButton.setVisible(true);
    }
  }

  // endregion

  // region =====Event Methods=====

  public void sendOKSignal() {
    sendSignal(AppTempVariable.SIGNAL_OK);
  }

  public void sendYesSignal() {
    sendSignal(AppTempVariable.SIGNAL_YES);
  }

  public void sendNoSignal() {
    sendSignal(AppTempVariable.SIGNAL_NO);
  }

  private void sendSignal(AppTempVariable signal) {
    logger.info("Sending the signal: " + signal);
    App.addTempVariable(signal, true);
    closeDialog();
  }

  // endregion
}
