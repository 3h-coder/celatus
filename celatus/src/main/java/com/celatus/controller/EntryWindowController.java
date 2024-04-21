package com.celatus.controller;

import com.celatus.handler.AuthHandler;
import com.celatus.models.TextInputValueTracker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

  private String password;

  private TextInputValueTracker passwordValueTracker;

  private boolean userInput;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    passwordValueTracker = new TextInputValueTracker();
    addOnKeyPressedListeners();
    addTextChangedListeners();
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

  private void addOnKeyPressedListeners() {
    pwdField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();
      if (event.isControlDown() && (eventCode == KeyCode.Z || eventCode == KeyCode.Y)) {
        event.consume();
      }
    });

    revealedPwdField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();
      if (event.isControlDown() && (eventCode == KeyCode.Z || eventCode == KeyCode.Y)) {
        event.consume();
      }
    });
  }

  private void addTextChangedListeners() {
    pwdField.textProperty().addListener(
        (observable, oldValue, newValue) -> {
          password = newValue;
          // if (userInput) {
          // passwordValueTracker.registerNewValue(password);
          // }
          revealedPwdField.setText(password);
        });
    revealedPwdField.textProperty().addListener(
        (observable, oldValue, newValue) -> {
          password = newValue;
          // if (userInput) {
          // passwordValueTracker.registerNewValue(password);
          // }
          pwdField.setText(password);
        });
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
