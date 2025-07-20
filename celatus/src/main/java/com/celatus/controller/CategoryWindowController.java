package com.celatus.controller;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.models.Category;
import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/** Controller of the window used to create and edit categories */
public class CategoryWindowController extends DialogWindowController {

  // region =====Variables

  @FXML
  private Label title;
  @FXML
  private Label errorLabel;
  @FXML
  private TextField nameTextField;
  @FXML
  private TextArea descriptionTextArea;

  private Category inputCategory;

  // endregion

  // region =====Getters and Setters=====

  public Category getInputCategory() {
    return inputCategory;
  }

  public void setInputCategory(Category category) {
    inputCategory = category;
  }

  // endregion

  // region =====Window Methods=====

  @Override
  protected void lateInitialize() {
    super.lateInitialize();
    fillFields();
  }

  /** Fills the window fields in case we're editing an existing category */
  private void fillFields() {
    if (inputCategory != null) {
      nameTextField.setText(inputCategory.getName());

      if (inputCategory.getDescription() != null) {
        descriptionTextArea.setText(inputCategory.getDescription());
      }
    }
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  // endregion

  // region =====Event Methods=====

  @FXML
  private void checkName(KeyEvent event) {
    int maxLength = 50;
    String currentName = nameTextField.getText();
    if (currentName.length() >= maxLength) {
      nameTextField.setText(currentName.substring(0, maxLength));
      nameTextField.positionCaret(maxLength);
    }
  }

  @FXML
  private void checkDescription(KeyEvent event) {
    int maxLength = 250;
    // We don't allow descriptions over 250 characters long
    String currentText = descriptionTextArea.getText();
    if (currentText.length() >= maxLength) {
      descriptionTextArea.setText(currentText.substring(0, maxLength));
      descriptionTextArea.positionCaret(maxLength);
    }
  }

  /**
   * Saves our category into the database (or updates it)
   */
  @FXML
  private void saveCategory() {
    Scene appScene = owner.getScene();
    @SuppressWarnings("unchecked")
    ListView<String> categoriesList = (ListView<String>) appScene.lookup("#categoriesList");
    AnchorPane descriptionPane = (AnchorPane) appScene.lookup("#descriptionPane");

    String name = nameTextField.getText();
    String description = descriptionTextArea.getText();
    // Checking required fields
    if (StringUtils.isBlank(name)) {
      errorLabel.setText("This field is required");
      return;
    }

    if (inputCategory == null) {

      var newCategory = new Category(name, description, null);
      addNewcategory(newCategory, categoriesList);

    } else {
      editExistingCategory(name, description, categoriesList, descriptionPane);
    }
  }

  private void addNewcategory(Category newCategory, ListView<String> categoriesList) {
    var passwordsDatabase = App.getPasswordsDatabase();
    var categoryName = newCategory.getName();

    try {
      passwordsDatabase.addCategory(newCategory);
      // Updating the main window categories list
      FXMLUtils.addToListView(categoriesList, categoryName);
    } catch (IllegalArgumentException ex) {
      errorLabel.setText("This category already exists");
      return;
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }

    // Adding the creation to the action tracker
    App.getActionTracker().addCatCreation(newCategory);

    // Close the window and notify the user
    closeDialog();
    summonNotificationPopup(App.getWindow(), "The category " + categoryName + " has been added");
  }

  private void editExistingCategory(String newName, String newDescription, ListView<String> categoriesList,
      AnchorPane descriptionPane) {
    var oldName = inputCategory.getName();

    // Updating the category
    inputCategory.setName(newName);
    inputCategory.setDescription(newDescription);
    inputCategory.setLastEditDate(LocalDateTime.now());

    // Updating the main window categories list and category description
    FXMLUtils.updateListView(categoriesList, oldName, newName);
    if (StringUtils.isNotBlank(newDescription)) {
      MainWindowController controller = (MainWindowController) App.getController();
      controller.showDescription(newDescription);
    } else {
      descriptionPane.setVisible(false);
    }

    // Close the window and notify the user
    closeDialog();
    summonNotificationPopup(App.getWindow(), "Category updated");
  }

  // endregion

}
