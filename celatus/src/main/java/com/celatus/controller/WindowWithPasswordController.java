package com.celatus.controller;

import org.apache.commons.lang3.StringUtils;

import com.celatus.models.TextInputValueTracker;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class WindowWithPasswordController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    protected PasswordField pwdField;
    @FXML
    protected TextField revealedPwdField;

    protected String password;
    protected TextInputValueTracker passwordValueTracker; // To re-implement the Ctrl+Z/Y for the password input
    protected boolean allowInputRegistration; // Used to properly save the values to be Ctrl+Z/Y'ed

    // endregion

    // region =====Window Methods=====

    protected void setUpPasswordFields() {
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
