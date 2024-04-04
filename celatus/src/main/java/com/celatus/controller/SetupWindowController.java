package com.celatus.controller;

import java.util.Map;

import com.celatus.App;
import com.celatus.handler.AuthHandler;
import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/** Controller of our setup window, used to (re)set our App's master password */
public class SetupWindowController extends DialogWindowController {

  // region =====Variables=====

  @FXML
  private PasswordField pwdField;
  @FXML
  private PasswordField pwdField2;
  @FXML
  private Button viewButton;
  @FXML
  private Button viewButton2;
  @FXML
  private Button enterButton;
  @FXML
  private TextField revealedPwdField;
  @FXML
  private TextField revealedPwdField2;
  @FXML
  private Label label01;
  @FXML
  private Label label02;
  @FXML
  private Label label03;
  @FXML
  private Label label04;

  private String password;
  private String password2;
  private String rollbackPassword2; // used to prevent copy pasting into the confirm master password field

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    pwdField2.setContextMenu(new ContextMenu());
    // Dynamic password update
    pwdField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              password = newValue;
              revealedPwdField.setText(password);
            });
    revealedPwdField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              password = newValue;
              pwdField.setText(password);
            });
    pwdField2
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              rollbackPassword2 = oldValue;
              password2 = newValue;
              revealedPwdField2.setText(password2);
            });
    revealedPwdField2
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              rollbackPassword2 = oldValue;
              password2 = newValue;
              pwdField2.setText(password2);
            });
  }

  @FXML
  public void warning(String message) {
    label04.setText(message);
    label04.setVisible(true);
  }

  @FXML
  public void displayMinimizeAndCloseButtons() {
    FXMLUtils.showElements(closeButton, minimizeButton);
  }

  /**
   * Method used to open the access to the App's main window after checking the
   * submitted master
   * passwords.
   */
  @FXML
  private void submitMasterPasswords() {
    // We check the master password
    Map<Boolean, String> passwordsCheckInfo = AuthHandler.checkPasswords(password, password2);
    boolean validPasswords = (boolean) passwordsCheckInfo.keySet().toArray()[0];
    if (!validPasswords) {
      String invalidPasswordsMessage = (String) passwordsCheckInfo.values().toArray()[0];
      warning(invalidPasswordsMessage);
      return;
    }

    // We open the app, or go back to it
    try {
      if (App.getSignal("master_password_reset_signal")) {
        AuthHandler.setAppEntry(password, true);
        closeDialog();
        logger.info("Master password changed");
        summonNotificationPopup(App.getWindow(), "Master password successfully changed");
      } else {
        AuthHandler.setAppEntry(password, false);
        switchToMainWindow();
      }
    } catch (Exception ex) {
      App.error(this.window, ex, "An unexpected error occured", logger, AlertMode.OK, true);
      close();
    }
  }

  @FXML
  private void goToPwdField2() {
    if ("View".equals(viewButton2.getText())) {
      pwdField2.requestFocus();
    } else {
      revealedPwdField2.requestFocus();
    }
  }

  // endregion

  // region =====Event Methods=====

  @FXML
  private void pwdFieldKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.TAB) {
      goToPwdField2();
    }
  }

  @FXML
  private void pwdField2KeyPressed(KeyEvent event) {
    // Prevent copy pasting into this field
    if (event.isControlDown() && event.getCode() == KeyCode.V) {
      warning("Copy pasting is not allowed in this field");
      pwdField2.setText(rollbackPassword2);
      pwdField2.positionCaret(rollbackPassword2.length());
    }
  }

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

  @FXML
  private void viewButton2Clicked() {
    if ("View".equals(viewButton2.getText())) {
      viewButton2.setText("Hide");
      pwdField2.setVisible(false);
      revealedPwdField2.setVisible(true);
    } else {
      viewButton2.setText("View");
      revealedPwdField2.setVisible(false);
      pwdField2.setVisible(true);
    }
  }

  // endregion

}
