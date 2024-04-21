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

  private boolean allowInputRegistration;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    passwordValueTracker = new TextInputValueTracker("");
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

  // region -----Listeners-----

  private void addOnKeyPressedListeners() {
    pwdField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();
      // ctrl+Z/Y detected
      if (event.isControlDown() && (eventCode == KeyCode.Z || eventCode == KeyCode.Y)) {
        event.consume();
        allowInputRegistration = false;

        if (eventCode == KeyCode.Z) {
          var previousValue = passwordValueTracker.getPreviousValue();
          if (previousValue == null) {
            return;
          }
          // update the text -> this will then trigger the text property changed listener
          pwdField.setText(previousValue);
          pwdField.positionCaret(previousValue.length());
        } else {
          var nextValue = passwordValueTracker.getNextValue();
          if (nextValue == null) {
            return;
          }
          pwdField.setText(nextValue);
          pwdField.positionCaret(nextValue.length());
        }
        return;
      }
      // let it be considered a user input to be registered
      allowInputRegistration = true;
    });

    revealedPwdField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();
      if (event.isControlDown() && (eventCode == KeyCode.Z || eventCode == KeyCode.Y)) {
        event.consume();
        allowInputRegistration = false;
        if (eventCode == KeyCode.Z) {
          var previousValue = passwordValueTracker.getPreviousValue();
          if (previousValue == null) {
            return;
          }
          revealedPwdField.setText(previousValue);
          revealedPwdField.positionCaret(previousValue.length());
        } else {
          var nextValue = passwordValueTracker.getNextValue();
          if (nextValue == null) {
            return;
          }
          revealedPwdField.setText(nextValue);
          revealedPwdField.positionCaret(nextValue.length());
        }
        return;
      }
      allowInputRegistration = true;
    });
  }

  private void addTextChangedListeners() {
    pwdField.textProperty().addListener(
        (observable, oldValue, newValue) -> {
          password = newValue;
          if (allowInputRegistration) {
            passwordValueTracker.registerNewValue(password);
            allowInputRegistration = false;
          }
          revealedPwdField.setText(password);
        });
    revealedPwdField.textProperty().addListener(
        (observable, oldValue, newValue) -> {
          password = newValue;
          if (allowInputRegistration) {
            passwordValueTracker.registerNewValue(password);
            allowInputRegistration = false;
          }
          pwdField.setText(password);
        });
  }

  // endregion

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
