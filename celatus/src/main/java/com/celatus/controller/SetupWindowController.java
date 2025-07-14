package com.celatus.controller;

import java.util.Map;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.enums.AppTempVariable;
import com.celatus.handler.AuthHandler;
import com.celatus.handler.DatabaseHandler;
import com.celatus.models.FxmlHelpers.PasswordPackageProcessor;
import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller of our setup window, used to (re)set our App's master password,
 * and set the location of the password file (the first time only).
 */
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
  private TextField pwdFileLocationField;
  @FXML
  private Button pwdFileLocationBrowseButton;
  @FXML
  private Button saveButton;
  @FXML
  private TextField revealedPwdField;
  @FXML
  private TextField revealedPwdField2;
  @FXML
  private Label label00;
  @FXML
  private Label label01;
  @FXML
  private Label label02;
  @FXML
  private Label label03;
  @FXML
  private Label label04;
  @FXML
  private Label errorLabel;

  private PasswordPackageProcessor pwdPackageProcessor;
  private PasswordPackageProcessor pwdPackageProcessor2;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    // No context menu for the second password field to prevent copy-pasting
    pwdField2.setContextMenu(new ContextMenu());
    pwdFileLocationField.setText(DatabaseHandler.getDBFolderPath());
    setUpPasswordPackages();
    addEventFilters();
  }

  @FXML
  public void warning(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
  }

  @FXML
  public void displayMinimizeAndCloseButtons() {
    FXMLUtils.showElements(closeButton, minimizeButton);
  }

  @FXML
  public void hidePasswordLocationFields() {
    FXMLUtils.collapseElements(label04, pwdFileLocationField, pwdFileLocationBrowseButton);
  }

  @FXML
  public void setBorderVisible() {
    rootPane.setStyle("-fx-border-width: 1");
  }

  /**
   * Method used to open the access to the App's main window after checking the
   * submitted master
   * passwords.
   */
  @FXML
  private void submitMasterPasswords() {
    // We check the master password
    Map<Boolean, String> passwordsCheckInfo = AuthHandler.checkPasswords(pwdPackageProcessor.getPassword(),
        pwdPackageProcessor2.getPassword());
    boolean validPasswords = (boolean) passwordsCheckInfo.keySet().toArray()[0];
    if (!validPasswords) {
      String invalidPasswordsMessage = (String) passwordsCheckInfo.values().toArray()[0];
      warning(invalidPasswordsMessage);
      return;
    }
    // We check and set the password file location
    String pwdFileLocation = pwdFileLocationField.getText();
    try {
      DatabaseHandler.setDBFolderPath(pwdFileLocation);
    } catch (Exception e) {
      warning("The password file location is invalid");
      return;
    }

    // We open the app, or go back to it
    try {
      if (App.getSignal(AppTempVariable.SIGNAL_MASTER_PASSWORD_RESET)) {
        AuthHandler.setAppEntry(pwdPackageProcessor.getPassword(), true);
        closeDialog();
        logger.info("Master password changed");
        summonNotificationPopup(App.getWindow(), "Master password successfully changed");
      } else {
        AuthHandler.setAppEntry(pwdPackageProcessor2.getPassword(), false);
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

  private void setUpPasswordPackages() {
    pwdPackageProcessor = new PasswordPackageProcessor(pwdField, revealedPwdField);
    pwdPackageProcessor2 = new PasswordPackageProcessor(pwdField2, revealedPwdField2);
    pwdPackageProcessor.setUpPasswordFields();
    pwdPackageProcessor2.setUpPasswordFields();
  }

  private void addEventFilters() {
    // Prevent copy-pasting in the second password field
    pwdField2.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.isControlDown() && event.getCode() == KeyCode.V) {
        warning("Copy-pasting is not allowed in this field");
        event.consume();
      }
    });
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
  private void viewButtonClicked() {
    handleViewButtonClicked(viewButton, pwdField, revealedPwdField);
  }

  @FXML
  private void viewButton2Clicked() {
    handleViewButtonClicked(viewButton2, pwdField2, revealedPwdField2);
  }

  private static void handleViewButtonClicked(Button viewButton,
      PasswordField pwdField, TextField revealedPwdField) {
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
  private void openFolderDialogForPwdLocation() {
    PwdsFileLocationWindowController.OpenPwdsFileLocationDialog(pwdFileLocationField, window);
  }

  // endregion

}
