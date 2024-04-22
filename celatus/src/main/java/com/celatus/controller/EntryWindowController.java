package com.celatus.controller;

import com.celatus.handler.AuthHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller of our entry window, after the master password has been set by the
 * user
 */
public class EntryWindowController extends WindowWithPasswordController {

  // region =====Variables=====

  @FXML
  private Button closeButton;
  @FXML
  private Button entryButton;
  @FXML
  protected Button viewButton;
  @FXML
  private Label label01;
  @FXML
  private Label label02;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    setUpPasswordFields();
  }

  @FXML
  public void warning(String message) {
    label02.setText(message);
    label02.setVisible(true);
  }

  @FXML
  private void getPasswordValue() {
    if ("View".equals(viewButton.getText())) {
      password = pwdField.getText();
    } else {
      password = revealedPwdField.getText();
    }
  }

  /** Method used to enter the App */
  @FXML
  private void submitPassword() {
    getPasswordValue();

    if (AuthHandler.correctPassword(password)) {
      AuthHandler.enterApp();
      switchToMainWindow();
    } else {
      warning("Incorrect master password");
    }
  }

  // endregion

  // region =====Event Methods=====

  @FXML
  private void viewButtonClicked() {
    if ("View".equals(viewButton.getText())) {
      viewButton.setText("Hide");
      pwdField.setVisible(false);
      revealedPwdField.setVisible(true);
    } else {
      viewButton.setText("View");
      revealedPwdField.setVisible(false);
      pwdField.setVisible(true);
    }
  }

  // endregion

}
