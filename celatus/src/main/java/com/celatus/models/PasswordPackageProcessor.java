package com.celatus.models;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Model class to handle all the set up regarding PasswordField + TextField
 * couples
 */
public class PasswordPackageProcessor {
    // region =====Variables=====

    @FXML
    private PasswordField pwdField;
    @FXML
    private TextField revealedPwdField;

    private String password; // The actual password value
    private TextInputValueTracker passwordValueTracker; // To re-implement the Ctrl+Z/Y for the password input
    private boolean allowInputRegistration; // Used to properly save the values to be Ctrl+Z/Y'ed

    // endregion

    // region ====Getters and Setters=====

    public String getPassword() {
        return password;
    }

    // endregion

    // region =====Constructor=====

    public PasswordPackageProcessor(PasswordField pwdField, TextField revealedPwdField) {
        this.pwdField = pwdField;
        this.revealedPwdField = revealedPwdField;
    }

    // endregion

    // region =====Window Methods=====

    public void setUpPasswordFields() {
        var initialPwdValue = StringUtils.isBlank(pwdField.getText()) ? "" : pwdField.getText();
        passwordValueTracker = new TextInputValueTracker(initialPwdValue);
        addOnKeyPressedListeners();
        addTextChangedListeners();
    }

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
}
