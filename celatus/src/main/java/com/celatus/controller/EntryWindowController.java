package com.celatus.controller;

import com.celatus.handler.AuthHandler;
import com.celatus.models.PasswordPackageProcessor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller of our entry window, after the master password has been set by the
 * user
 */
public class EntryWindowController extends BaseWindowController {

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
  @FXML
  private PasswordField pwdField;
  @FXML
  private TextField revealedPwdField;

  private PasswordPackageProcessor pwdPackageProcessor;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    pwdPackageProcessor = new PasswordPackageProcessor(pwdField, revealedPwdField);
    pwdPackageProcessor.setUpPasswordFields();
  }

  @FXML
  public void warning(String message) {
    label02.setText(message);
    label02.setVisible(true);
  }

  /** Method used to enter the App */
  @FXML
  private void submitPassword() {

    if (AuthHandler.correctPassword(pwdPackageProcessor.getPassword())) {
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
