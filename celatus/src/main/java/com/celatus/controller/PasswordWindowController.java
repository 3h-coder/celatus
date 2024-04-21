package com.celatus.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.models.Category;
import com.celatus.models.PasswordEntry;
import com.celatus.models.RecordEntryView;
import com.celatus.models.TextInputValueTracker;
import com.celatus.util.CryptoUtils;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/** Controller of the window used to create and edit password entries */
public class PasswordWindowController extends DialogWindowController {

  // region =====Variables=====

  @FXML
  private AnchorPane rowPane2;
  @FXML
  private Label title;
  @FXML
  private TextField nameTextField;
  @FXML
  private TextField urlField;
  @FXML
  private TextField identifierField;
  @FXML
  private TextField emailField;
  @FXML
  private PasswordField pwdField;
  @FXML
  private TextField revealedPwdField;
  @FXML
  private TextArea passwordNotes;
  @FXML
  private Label createdLabel;
  @FXML
  private Label lastEditedLabel;
  @FXML
  private Label checkNameLabel;
  @FXML
  private Label checkIdentifierLabel;
  @FXML
  private Label checkPasswordLabel;
  @FXML
  private Label nameLabel;
  @FXML
  private Label urlLabel;
  @FXML
  private Label identifierLabel;
  @FXML
  private Label emailLabel;
  @FXML
  private Label passwordLabel;
  @FXML
  private Label notesLabel;
  @FXML
  private Button viewButton;
  @FXML
  private Button generatePwdButton;
  @FXML
  private Button saveButton;
  @FXML
  private Button recordsButton;
  @FXML
  private Button viewRecordButton;
  @FXML
  private TableView<RecordEntryView> recordsTable;
  @FXML
  private TableColumn<RecordEntryView, String> dateColumn;
  @FXML
  private TableColumn<RecordEntryView, String> changesColumn;

  private Category category; // the password entry's category
  private PasswordEntry inputPwdEntry; // the password entry we are editing
  private String password; // the current password (displayed on the window)
  private boolean recordMode; // whether we are in recordMode or not (recordMode refers to when we hide the
  // default elements to view the password entry's history)
  private List<Node> defaultModeElements; // list where we store the default UI elements
  private List<Node> recordModeElements; // list where we store the record mode UI elements
  private String detectedChanges; // detected changes from the user (what is on the UI vs what is stored in the
                                  // object)
  private TextInputValueTracker passwordValueTracker; // To re-implement the Ctrl+Z/Y for the password input
  private boolean allowPwdInputRegistration; // Used to properly save the values to be Ctrl+Z/Y'ed

  // endregion

  // region =====Getters and Setters=====

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public PasswordEntry getInputPwdEntry() {
    return inputPwdEntry;
  }

  public void setInputPwdEntry(PasswordEntry pwdEntry) {
    this.inputPwdEntry = pwdEntry;
  }

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {
    super.initialize();
    Platform.runLater(
        () -> {
          fillFields();
          // We add our listeners
          addListeners();
          // We set the record mode to false
          recordMode = false;
          // We set the mode items
          setModesItems();
          // We set the record button
          setRecordButton();
          // we intialize the detected changes
          detectedChanges = "";
        });
  }

  private void addListeners() {
    // Dynamic password updates
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
    // recordsTable
    recordsTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldSelection, newSelection) -> {
              if (recordsTable.getSelectionModel().getSelectedItems().isEmpty()) {
                // No item is selected, disable the button
                viewRecordButton.setDisable(true);
              } else {
                // At least one item is selected, enable the button
                viewRecordButton.setDisable(false);
              }
            });
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  /** Adds the concerned items in the appropriate mode array */
  public void setModesItems() {
    // default mode
    defaultModeElements = new ArrayList<Node>();

    Collections.addAll(
        defaultModeElements,
        nameTextField,
        urlField,
        identifierField,
        emailField,
        pwdField,
        passwordNotes,
        createdLabel,
        lastEditedLabel,
        checkNameLabel,
        checkIdentifierLabel,
        checkPasswordLabel,
        nameLabel,
        urlLabel,
        identifierLabel,
        emailLabel,
        passwordLabel,
        notesLabel,
        saveButton,
        viewButton,
        generatePwdButton);

    // records mode
    recordModeElements = new ArrayList<Node>();

    Collections.addAll(recordModeElements, recordsTable, viewRecordButton);
  }

  /** Fills the window fields in case we're editing an existing password entry */
  public void fillFields() {
    if (inputPwdEntry == null) {
      return;
    }

    nameTextField.setText(inputPwdEntry.getName());
    urlField.setText(inputPwdEntry.getUrl());
    identifierField.setText(inputPwdEntry.getIdentifier());
    emailField.setText(inputPwdEntry.getEmail());
    this.password = inputPwdEntry.getPassword();
    pwdField.setText(inputPwdEntry.getPassword());
    revealedPwdField.setText(inputPwdEntry.getPassword());
    passwordNotes.setText(inputPwdEntry.getNotes());
    createdLabel.setText(
        "Created : " + CustomDateUtils.prettyDate(inputPwdEntry.getCreationDate()));
    lastEditedLabel.setText(
        "Last edited : " + CustomDateUtils.prettyDate(inputPwdEntry.getLastEditDate()));
  }

  public void setRecordButton() {
    if (inputPwdEntry == null || inputPwdEntry.getRecords() == null) {
      recordsButton.setDisable(true);
    }
  }

  public void fillRecordsTable() {
    if (inputPwdEntry == null || inputPwdEntry.getRecords() == null) {
      return;
    }

    // Convert the records map into ObservableList of RecordEntry
    ObservableList<RecordEntryView> recordEntries = FXCollections.observableArrayList();
    inputPwdEntry
        .getRecords()
        .forEach(
            (date, record) -> recordEntries.add(new RecordEntryView(date, (String) ((Map) record).get("changes"))));

    recordsTable.setItems(recordEntries);

    // Bind the columns to the data
    dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    changesColumn.setCellValueFactory(cellData -> cellData.getValue().changesProperty());

    // Sorting our records from newest to oldest
    recordsTable.getSortOrder().add(dateColumn);
    dateColumn.setSortType(TableColumn.SortType.DESCENDING);
    recordsTable.sort();
  }

  // endregion

  // region =====Event Methods=====

  /**
   * Saves our password entry into the database, or updates it if it's an existing
   * one
   */
  @FXML
  private void savePassword() {

    // We first check that none of the required fiels is blank
    if (!requiredFieldsNotBlank()) {
      return;
    }
    // We then check that the fields are valid
    /*
     * if (!allFieldsValid()) {
     * return;
     * }
     */

    String name = nameTextField.getText();
    String identifier = identifierField.getText();
    String url = urlField.getText();
    String email = emailField.getText();
    String notes = passwordNotes.getText();

    MainWindowController controller = (MainWindowController) App.getController();

    // Saving the new password entry
    if (inputPwdEntry == null) {
      PasswordEntry pwdEntry = new PasswordEntry(name, url, notes, identifier, email, this.password);
      this.category.addPasswordEntry(pwdEntry);
      // Add the creation to the action tracker
      App.getActionTracker().addPwdCreation(pwdEntry, this.category.getName());
      logger.info(
          "Adding the following password entry to the "
              + this.category.getName()
              + "category : "
              + pwdEntry);
      // Updating the exisiting password entry
    } else if (changesDetected()) {

      // We save the record and changes first
      inputPwdEntry.saveRecord(detectedChanges);

      // We update the password entry
      inputPwdEntry.setName(name);
      inputPwdEntry.setIdentifier(identifier);
      inputPwdEntry.setUrl(url);
      inputPwdEntry.setPassword(this.password);
      inputPwdEntry.setEmail(email);
      inputPwdEntry.setNotes(notes);
      inputPwdEntry.setLastEditDate(LocalDateTime.now());

      closeDialog();
      summonNotificationPopup(App.getWindow(), "Password entry updated");
    }
    closeDialog();
    // Refreshing the password entry view in the main window
    controller.displayPasswords(category);
  }

  @FXML
  private void viewButtonClicked() {
    if ("View".equals(viewButton.getText())) {
      viewButton.setText("Hide");
      pwdField.setVisible(false);
      revealedPwdField.setVisible(true);
      revealedPwdField.setText(password);
    } else {
      viewButton.setText("View");
      revealedPwdField.setVisible(false);
      pwdField.setVisible(true);
      pwdField.setText(password);
    }
  }

  @FXML
  private void generatePwd() {
    String generatedPwd = CryptoUtils.generateRandomPwd(16);
    pwdField.setText(generatedPwd);
  }

  @FXML
  private void recordsButtonClicked() {
    // We invert the recordMode's value (false to true, true to false)
    recordMode = !recordMode;
    if (recordMode) {
      FXMLUtils.hideElements(defaultModeElements.toArray(new Node[defaultModeElements.size()]));
      FXMLUtils.showElements(recordModeElements.toArray(new Node[recordModeElements.size()]));
      fillRecordsTable();
      recordsButton.setText("\u2190 Back"); // ← Back
    } else {
      FXMLUtils.showElements(defaultModeElements.toArray(new Node[defaultModeElements.size()]));
      FXMLUtils.hideElements(recordModeElements.toArray(new Node[recordModeElements.size()]));
      recordsButton.setText("History");
    }
  }

  @FXML
  private void viewRecordButtonClicked() {
    String selectedDate = recordsTable.getSelectionModel().getSelectedItem().getDate();
    Map<String, String> foundRecord = findRecord(selectedDate);
    if (foundRecord != null) {
      App.addTempVariable("password_record", foundRecord);
      var coordinates = FXMLUtils.findOuterCoordinatesForWindow(window, 40, 510, 420);
      launchWindow("viewPasswordWindow", coordinates.get("X"), coordinates.get("Y"));
    }
  }

  // region -----Utils-----

  /**
   * Separate method to check that all of our required fields are not blank
   *
   * @return <b>true</b> if no field is blank, <b>false</b> otherwise
   */
  @FXML
  private boolean requiredFieldsNotBlank() {

    boolean allOk = true;

    checkNameLabel.setText(null);
    String name = nameTextField.getText();
    if (StringUtils.isBlank(name)) {
      checkNameLabel.setText("This field is required");
      allOk = false;
    }

    checkIdentifierLabel.setText(null);
    String identifier = identifierField.getText();
    if (StringUtils.isBlank(identifier)) {
      checkIdentifierLabel.setText("This field is required");
      allOk = false;
    }

    checkPasswordLabel.setText(null);
    if (StringUtils.isBlank(this.password)) {
      checkPasswordLabel.setText("This field is requied");
      allOk = false;
    }

    return allOk;
  }

  /**
   * Checks for the validity of certain fields
   *
   * @return
   */
  @FXML
  private boolean allFieldsValid() {
    boolean allOK = true;

    checkNameLabel.setText(null);
    String name = nameTextField.getText();
    if (name.startsWith("-")) {
      checkNameLabel.setText("The name cannot start with '-'");
      allOK = false;
    }

    return allOK;
  }

  /**
   * Detects whether changes have been made or not to the password entry (in case
   * of editing)
   *
   * @return
   */
  private boolean changesDetected() {

    String name = nameTextField.getText();
    String identifier = identifierField.getText();
    String url = urlField.getText();
    String email = emailField.getText();
    String notes = passwordNotes.getText();

    if (!StringUtils.equals(inputPwdEntry.getName(), name)) {
      detectedChanges += "name, ";
    }
    if (!StringUtils.equals(inputPwdEntry.getIdentifier(), identifier)) {
      detectedChanges += "identifier, ";
    }
    if (!StringUtils.equals(inputPwdEntry.getUrl(), url)) {
      detectedChanges += "url, ";
    }
    if (!StringUtils.equals(inputPwdEntry.getEmail(), email)) {
      detectedChanges += "email, ";
    }
    if (!StringUtils.equals(inputPwdEntry.getNotes(), notes)) {
      detectedChanges += "notes, ";
    }
    if (!StringUtils.equals(inputPwdEntry.getPassword(), password)) {
      detectedChanges += "password, ";
    }

    if (detectedChanges.contains(",")) {
      detectedChanges = detectedChanges.substring(0, detectedChanges.lastIndexOf(","));
    }
    return StringUtils.isNotBlank(detectedChanges);
  }

  private Map<String, String> findRecord(String dateString) {
    if (inputPwdEntry == null || inputPwdEntry.getRecords() == null) {
      return null;
    }

    Map<String, String> record = null;
    for (var entry : inputPwdEntry.getRecords().entrySet()) {
      if (entry.getKey() != dateString) {
        continue;
      }

      record = entry.getValue();
    }
    return record;
  }
  // endregion

  // endregion

}
