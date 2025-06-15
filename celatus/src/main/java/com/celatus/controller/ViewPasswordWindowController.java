package com.celatus.controller;

import java.util.Map;

import com.celatus.App;
import com.celatus.enums.AppTempVariable;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Controller of our viewPassword window */
public class ViewPasswordWindowController extends BaseWindowController {

  // region =====Variables=====

  @FXML
  private TextField nameTextField;
  @FXML
  private TextField urlField;
  @FXML
  private TextField identifierField;
  @FXML
  private TextField emailField;
  @FXML
  private TextField revealedPwdField;
  @FXML
  private TextArea passwordNotes;

  private Map<String, String> passwordRecord;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    Platform.runLater(
        () -> {
          fillFields();
        });
  }

  @SuppressWarnings("unchecked")
  public void fillFields() {
    // extract the password record
    this.passwordRecord = (Map<String, String>) App.extractTempVariable(AppTempVariable.PASSWORD_RECORD);

    // fill in the fields
    nameTextField.setText(passwordRecord.get("name"));
    urlField.setText(passwordRecord.get("url"));
    identifierField.setText(passwordRecord.get("identifier"));
    emailField.setText(passwordRecord.get("email"));
    revealedPwdField.setText(passwordRecord.get("password"));
    passwordNotes.setText(passwordRecord.get("notes"));
  }

  // endregion

  // region =====Event Methods=====

  // endregion
}
