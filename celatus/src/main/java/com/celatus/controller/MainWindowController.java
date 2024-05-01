package com.celatus.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.enums.Signal;
import com.celatus.enums.UserSettings;
import com.celatus.handler.DatabaseHandler;
import com.celatus.handler.SearchHandler;
import com.celatus.models.Category;
import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.DesktopUtils;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/** The controller of the App's main window */
public class MainWindowController extends BaseWindowController {

  // region =====Variables=====

  @FXML
  private AnchorPane columnPane1;
  @FXML
  private VBox columnPane2;
  @FXML
  private AnchorPane descriptionPane;
  @FXML
  private AnchorPane passwordsPane;
  @FXML
  private AnchorPane blankSpacePane;
  @FXML
  private TextArea catDescription;
  @FXML
  private TextField searchBar;
  @FXML
  private TableView<PasswordEntry> passwordsTable;
  @FXML
  private MenuBar menuBar;
  @FXML
  private Menu fileMenu;
  @FXML
  private Menu settingsMenu;
  @FXML
  private Menu helpMenu;
  @FXML
  private ListView<String> categoriesList;

  // endregion

  // region =====Window Methods=====

  @Override
  public void initialize() {

    super.initialize();
    Platform.runLater(
        () -> {
          performGraphicalSetup();
        });
    // logger.debug(App.getProperties());
    // logger.debug(App.getPasswordsDatabase());
  }

  /**
   * Performs all the graphical setup such as the window dimensions or the context
   * menus
   */
  private void performGraphicalSetup() {
    // We set the minimum window dimensions
    window.setMinWidth(700);
    window.setMinHeight(400);
    // We set the proper bindings
    setBindings();
    // We add our listeners
    addListeners();
    // We fill up the categories list view and set up the context menus
    displayCategories();
    // We set our context menus and passwords table
    setContextMenus();
    setPasswordsTable();
  }

  private void setBindings() {
    descriptionPane.maxHeightProperty().bind(catDescription.prefHeightProperty());
    descriptionPane.minHeightProperty().bind(catDescription.prefHeightProperty());
    passwordsPane.maxHeightProperty().bind(Bindings.divide(columnPane2.heightProperty(), 100).multiply(80));
  }

  private void addListeners() {
    categoriesList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String categoryName = newValue;
              if (StringUtils.isBlank(newValue)) {
                return;
              }
              showCategory(categoryName);
            });
    catDescription
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
              if (selectedCategory != null) {
                FXMLUtils.adjustTextAreaHeight(catDescription);
              }
            });
  }

  /**
   * Displays the category's description as well as all of its passwords
   *
   * @param categoryName
   */
  public void showCategory(String categoryName) {
    Category category = App.getPasswordsDatabase().getCategory(categoryName);
    String categoryDescription = category.getDescription();
    if (StringUtils.isNotBlank(categoryDescription)) {
      showDescription(categoryDescription);
    } else {
      catDescription.clear();
      catDescription.setPrefHeight(0);
    }
    // Displaying all the password entries
    displayPasswords(category);
  }

  /**
   * Displays the given description in the description pane
   *
   * @param description
   */
  @FXML
  public void showDescription(String description) {
    descriptionPane.setVisible(true);
    catDescription.setText(description);
    FXMLUtils.adjustTextAreaHeight(catDescription);
  }

  /** Displays all the categories of our database */
  private void displayCategories() {
    String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
    categoriesList.getItems().clear();
    for (Category category : App.getPasswordsDatabase().getCategories()) {
      FXMLUtils.addToListView(categoriesList, category.getName());
    }
    if (selectedCategory != null) {
      categoriesList.getSelectionModel().select(selectedCategory);
    }
  }

  /**
   * Fills the password table with the given category's password entries
   *
   * @param cat
   */
  private void fillPasswordsTable(Category cat) {
    for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
      FXMLUtils.addToTableView(passwordsTable, pwdEntry);
    }
  }

  /**
   * Fills the password table with the given password enties list
   *
   * @param passwordEntries
   */
  private void fillPasswordsTable(List<PasswordEntry> passwordEntries) {
    for (PasswordEntry pwdEntry : passwordEntries) {
      FXMLUtils.addToTableView(passwordsTable, pwdEntry);
    }
  }

  /**
   * Displays all the password entries of the selected category, refreshing the
   * view
   *
   * @param category
   */
  public void displayPasswords(Category category) {
    passwordsPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    passwordsTable.getItems().clear();
    passwordsTable.setPrefHeight(0);
    if (category.getPasswordEntries() != null) {
      passwordsTable.setVisible(true);
      fillPasswordsTable(category);
    } else {
      passwordsTable.setVisible(false);
    }
  }

  /**
   * Displays all the given password entries
   *
   * @param category
   */
  public void displayPasswords(List<PasswordEntry> passwordEntries) {
    passwordsTable.getItems().clear();
    passwordsTable.setPrefHeight(0);
    catDescription.setPrefHeight(0);
    if (passwordEntries != null && !passwordEntries.isEmpty()) {
      passwordsTable.setVisible(true);
      fillPasswordsTable(passwordEntries);
    } else {
      passwordsTable.setVisible(false);
    }
  }

  /**
   * Used whenever we resize the window, to prevent a glitch showing an empty
   * description
   */
  private void controlCatDescription() {
    if (StringUtils.isBlank(catDescription.getText())) {
      catDescription.setPrefHeight(0);
    } else {
      FXMLUtils.adjustTextAreaHeight(catDescription);
    }
  }

  // endregion

  // region =====Event Methods=====

  @Override
  public void close() {
    // Check for unsaved changes
    int originalDBHash = App.getOriginalDatabaseHash();
    int currentDBHash = App.getPasswordsDatabase().hashCode();
    if (originalDBHash == currentDBHash) {
      super.close();
    } else {
      App.warn(
          window,
          "Unsaved changes detected, would you like to save these changes?",
          logger,
          AlertMode.YES_AND_NO);
    }
    if (App.getSignal(Signal.YES)) {
      saveDatabase();
      super.close();
    } else if (App.getSignal(Signal.NO)) {
      super.close();
    }
  }

  @Override
  public void maximize() {
    super.maximize();
    controlCatDescription();
  }

  @Override
  public void windowKeyPressed(KeyEvent event) {
    super.windowKeyPressed(event);
    KeyCode eventCode = event.getCode();
    // Full screen
    if (eventCode == KeyCode.F11) {
      removeNotificationPopup();
      if (window.isMaximized()) {
        window.setMaximized(false);
        controlCatDescription();
      } else {
        window.setMaximized(true);
      }
    } else if (eventCode == KeyCode.ESCAPE && window.isMaximized()) {
      removeNotificationPopup();
      window.setMaximized(false);
      controlCatDescription();
    }
    // New category (only if there is a notification popup, otherwise it overlaps
    // with the accelerator)
    if (event.isShiftDown() && eventCode == KeyCode.C && notifPopupShown()) {
      openCatWindow();
    }
    // New password (same here)
    if (event.isShiftDown() && eventCode == KeyCode.P && notifPopupShown()) {
      openPwdWindow();
    }
    // Ctrl+Z
    if (event.isControlDown() && eventCode == KeyCode.Z) {
      App.getActionTracker().goBackwards();

      String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
      if (selectedCategory != null) {
        Category category = App.getPasswordsDatabase().getCategory(selectedCategory);
        displayPasswords(category);
      }

      displayCategories();
    }
    // Ctrl+Y
    if (event.isControlDown() && eventCode == KeyCode.Y) {
      App.getActionTracker().goForwards();

      String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
      if (selectedCategory != null) {
        Category category = App.getPasswordsDatabase().getCategory(selectedCategory);
        displayPasswords(category);
      }

      displayCategories();
    }
  }

  @FXML
  public void searchBarKeyPressed(KeyEvent event) {
    KeyCode eventCode = event.getCode();
    // String eventText = event.getText();

    if (eventCode == KeyCode.ESCAPE || eventCode == KeyCode.TAB) {
      // Disabling the onKeyTyped other wise it is triggered
      searchBar.setOnKeyTyped(keyTypedEvent -> {
      });
      searchBar.getParent().requestFocus();

      return;
    }
    // Re-enabling onKeyTyped if not the escape key
    searchBar.setOnKeyTyped(keyTypedEvent -> searchBarKeyTyped(keyTypedEvent));
  }

  @FXML
  public void searchBarKeyTyped(KeyEvent event) {
    displayPasswords(searchPassword());
  }

  @FXML
  public void searchBarOnAction() {
    var searchResult = searchPassword();
    if (searchResult == null) {
      return;
    }
    if (searchResult.isEmpty()) {
      summonNotificationPopup(window, "No password found");
      return;
    }
    displayPasswords(searchResult);
  }

  @FXML
  public void searchBarOnMouseClicked() {
    Popup popup = (Popup) App.getTempVariable("notification_popup");
    if (popup != null) {
      FXMLUtils.enableKeyTransfer((TextArea) popup.getContent().get(0), searchBar);
    }
  }

  /**
   * Searches for the given password, and displays all the passwords that contain
   * its name.
   *
   * @param passwordName
   */
  public List<PasswordEntry> searchPassword() {
    // We deselect the selected category
    categoriesList.getSelectionModel().clearSelection();
    return SearchHandler.searchPassword(searchBar.getText());
  }

  // endregion

  // region =====Back end / Menu Item Methods=====

  // region -----Categories and Password Entries-----

  /** Sets up the context menus for our categories and password entries */
  private void setContextMenus() {
    // Setting up the context menu for our categories
    setCatListViewContextMenu();
    // Setting up the context menu for our categories column pane
    setCatPaneContextMenu();
    // Setting up the context menu for our passwords table
    setPwdTableContextMenu();
    // Setting up the context menu for our passwords column pane
    setPwdPaneContextMenu();
  }

  /** Sets the context menu for our passwords pane */
  private void setPwdPaneContextMenu() {
    ContextMenu newPwdContextMenu = new ContextMenu();
    MenuItem newPwdMenuItem = new MenuItem("new password");

    newPwdMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN));
    newPwdMenuItem.setOnAction(
        event -> {
          openPasswordWindow(null);
        });

    newPwdContextMenu.getItems().add(newPwdMenuItem);
    blankSpacePane.addEventHandler(
        ContextMenuEvent.CONTEXT_MENU_REQUESTED,
        event -> {
          newPwdContextMenu.show(passwordsPane, event.getScreenX(), event.getScreenY());
          // we disable the new password creation if no category was selected
          for (MenuItem menuItem : newPwdContextMenu.getItems()) {
            if ("new password".equals(menuItem.getText())) {
              if (StringUtils.isBlank(this.categoriesList.getSelectionModel().getSelectedItem())) {
                menuItem.setDisable(true);
              } else {
                menuItem.setDisable(false);
              }
            }
          }
          event.consume();
        });
    columnPane2.addEventHandler(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          newPwdContextMenu.hide();
        });
    passwordsTable.addEventHandler(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          newPwdContextMenu.hide();
        });
  }

  /** Sets the context menu for our password entries table */
  private void setPwdTableContextMenu() {

    ContextMenu pwdContextMenu = new ContextMenu();
    MenuItem editPwdMenuItem = new MenuItem("edit");
    MenuItem deletePwdMenuItem = new MenuItem("delete");
    Menu movePwdMenuItem = new Menu("move to");
    MenuItem copyPwdMenuItem = new MenuItem("copy password");
    MenuItem copyIdMenuItem = new MenuItem("copy identifier");
    MenuItem openWebMenuItem = new MenuItem("open website url");

    editPwdMenuItem.setOnAction(
        event -> {
          PasswordEntry selectedPassword = getSelectedPassword();
          openPasswordWindow(selectedPassword);
        });

    deletePwdMenuItem.setOnAction(
        event -> {
          App.warn(
              this.window, "Are you sure you want to delete it?", logger, AlertMode.YES_AND_NO);
          if (App.getSignal(Signal.YES)) {
            PasswordEntry selectedPassword = getSelectedPassword();
            deletePasswordEntry(selectedPassword);
          }
        });

    copyPwdMenuItem.setOnAction(
        event -> {
          PasswordEntry selectedPassword = getSelectedPassword();
          copyPwdToClipBoard(selectedPassword.getPassword());
        });

    copyIdMenuItem.setOnAction(
        event -> {
          PasswordEntry selectedPassword = getSelectedPassword();
          copyIdToClipBoard(selectedPassword.getIdentifier());
        });

    openWebMenuItem.setOnAction(
        event -> {
          PasswordEntry selectedPassword = getSelectedPassword();
          String url = selectedPassword.getUrl();
          if (!url.startsWith("http")) {
            url = "https://" + url;
          }
          App.getHS().showDocument(url);
        });

    // Runtime context menu calculations
    this.passwordsTable.addEventHandler(
        ContextMenuEvent.CONTEXT_MENU_REQUESTED,
        event -> {
          PasswordEntry selectedPassword = getSelectedPassword();
          // No context menu if no selected password
          if (selectedPassword == null) {
            pwdContextMenu.getItems().clear();
            return;
            // If the context menu saw all of its items get erased, repopulate and show it
          } else if (pwdContextMenu.getItems().isEmpty()) {
            pwdContextMenu
                .getItems()
                .addAll(
                    editPwdMenuItem,
                    deletePwdMenuItem,
                    movePwdMenuItem,
                    copyPwdMenuItem,
                    copyIdMenuItem,
                    openWebMenuItem);
            pwdContextMenu.show(passwordsTable, event.getScreenX(), event.getScreenY());
          }
          // Adding all of our categories to the "move to" menu, (except the password's
          // one).
          String passwordCategory = selectedPassword.findCategory().getName();
          movePwdMenuItem.getItems().clear();
          for (Category category : App.getPasswordsDatabase().getCategories()) {
            String categoryName = category.getName();
            if (StringUtils.equals(passwordCategory, categoryName)) {
              continue;
            }
            MenuItem menuItem = new MenuItem(categoryName);
            menuItem.setOnAction(_event -> movePasswordEntry(selectedPassword, passwordCategory, categoryName));
            movePwdMenuItem.getItems().add(menuItem);
          }
          // Disabling the open url option if no url
          if (StringUtils.isBlank(selectedPassword.getUrl())) {
            openWebMenuItem.setDisable(true);
          } else {
            openWebMenuItem.setDisable(false);
          }
        });

    pwdContextMenu
        .getItems()
        .addAll(
            editPwdMenuItem,
            deletePwdMenuItem,
            movePwdMenuItem,
            copyPwdMenuItem,
            copyIdMenuItem,
            openWebMenuItem);

    passwordsTable.setContextMenu(pwdContextMenu);
  }

  private PasswordEntry getSelectedPassword() {
    return passwordsTable.getSelectionModel().getSelectedItem();
  }

  /** Sets the context menu of our categories pane */
  private void setCatPaneContextMenu() {
    ContextMenu newCatContextMenu = new ContextMenu();
    MenuItem newCatMenuItem = new MenuItem("new category");

    newCatMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHIFT_DOWN));
    newCatMenuItem.setOnAction(
        event -> {
          openCategoryWindow(null);
        });
    newCatContextMenu.getItems().add(newCatMenuItem);
    columnPane1.addEventHandler(
        ContextMenuEvent.CONTEXT_MENU_REQUESTED,
        event -> {
          newCatContextMenu.show(columnPane1, event.getScreenX(), event.getScreenY());
          event.consume();
        });
    columnPane1.addEventHandler(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          newCatContextMenu.hide();
        });
    categoriesList.addEventHandler(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          newCatContextMenu.hide();
        });
  }

  /** Sets the context menu for our categories list view */
  private void setCatListViewContextMenu() {
    this.categoriesList.setCellFactory(
        (listView) -> {
          ListCell<String> cell = new ListCell<>();
          cell.textProperty().bind(cell.itemProperty());

          MenuItem editMenuItem = new MenuItem("edit");
          MenuItem deleteMenuItem = new MenuItem("delete");
          MenuItem moveUpItem = new MenuItem("move up");
          MenuItem moveDownItem = new MenuItem("move down");

          editMenuItem.setOnAction(
              event -> {
                String categoryName = cell.getItem();
                Category category = App.getPasswordsDatabase().getCategory(categoryName);
                openCategoryWindow(category);
              });

          deleteMenuItem.setOnAction(
              event -> {
                App.warn(
                    window,
                    "This is an irreversible action, and you will lose all the passwords in this"
                        + " category, are you sure you want to delete it?",
                    logger,
                    AlertMode.YES_AND_NO);
                if (App.getSignal(Signal.YES) == true) {
                  String categoryName = cell.getItem();
                  FXMLUtils.removeFromListView(listView, categoryName);
                  deleteCategory(categoryName);
                  // clearing the selection
                  categoriesList.getSelectionModel().clearSelection();
                }
              });

          moveUpItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.SHIFT_DOWN));
          moveUpItem.setOnAction(
              event -> {
                moveCategoryUp();
              });

          moveDownItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHIFT_DOWN));
          moveDownItem.setOnAction(
              event -> {
                moveCategoryDown();
              });

          ContextMenu contextMenu = new ContextMenu();
          contextMenu.getItems().addAll(editMenuItem, deleteMenuItem, moveUpItem, moveDownItem);
          cell.setContextMenu(contextMenu);

          cell.setOnMouseClicked(
              event -> {
                String categoryName = cell.getItem();
                showCategory(categoryName);
              });

          return cell;
        });
  }

  /** Sets the password entries table, formatting the columns appropriately. */
  @SuppressWarnings("unchecked")
  private void setPasswordsTable() {

    TableColumn nameColumn = new TableColumn("Name");
    TableColumn identifierColumn = new TableColumn("Identifier");
    TableColumn lastEditedColumn = new TableColumn("Last edited");

    FXMLUtils.setMinWidth(100.0, nameColumn, identifierColumn, lastEditedColumn);

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
    lastEditedColumn.setCellValueFactory(new PropertyValueFactory<>("lastEditDate"));

    // Making the date readable
    lastEditedColumn.setCellFactory(
        column -> {
          return new TableCell<PasswordEntry, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
              super.updateItem(item, empty);
              if (item == null || empty) {
                setText(null);
              } else {
                setText(CustomDateUtils.prettyDate(item));
              }
            }
          };
        });

    TableColumn passwordColumn = ConstructPasswordColumn();

    passwordsTable
        .getColumns()
        .addAll(nameColumn, identifierColumn, passwordColumn, lastEditedColumn);
  }

  /**
   * Constructs the password column, taking into account the passwords visible
   * setting
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private TableColumn ConstructPasswordColumn() {
    TableColumn passwordColumn = new TableColumn("Password");
    passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    passwordColumn.setCellFactory(
        column -> {
          return new TableCell<PasswordEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
              super.updateItem(item, empty);
              if (item == null || empty) {
                setText(null);
              } else if (Boolean.valueOf(appProperties.getProperty(UserSettings.PASSWORDS_VISIBLE.toString()))) {
                setText(item);
              } else {
                setText("\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022");
              }
            }
          };
        });
    FXMLUtils.setMinWidth(100.0, passwordColumn);

    return passwordColumn;
  }

  /**
   * Opens a window to create a category or edit the selected category
   *
   * @param category
   */
  private void openCategoryWindow(Category category) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController("categoryWindow");
      Scene scene = (Scene) map.get("Scene");
      CategoryWindowController controller = (CategoryWindowController) map.get("Controller");

      String title;
      if (category != null) {
        title = "Editing category";
        controller.setInputCategory(category);
      } else {
        title = "Adding new category";
      }
      controller.setTitle(title);
      FXMLUtils.launchDialogWindow(this.window, scene);

    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  /**
   * Opens a window to create a password entry or edit the selected one
   *
   * @param pwdEntry
   */
  private void openPasswordWindow(PasswordEntry pwdEntry) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController("passwordWindow");
      Scene scene = (Scene) map.get("Scene");
      PasswordWindowController controller = (PasswordWindowController) map.get("Controller");

      String title;
      if (pwdEntry != null) {
        title = "Editing password entry";
        controller.setInputPwdEntry(pwdEntry);
      } else {
        title = "Adding new password entry";
      }
      controller.setCategory(
          App.getPasswordsDatabase()
              .getCategory(this.categoriesList.getSelectionModel().getSelectedItem()));
      controller.setTitle(title);
      FXMLUtils.launchDialogWindow(this.window, scene);
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  /**
   * Deletes a given category from the database
   *
   * @param categoryName : Name of the category to be deleted
   */
  private void deleteCategory(String categoryName) {
    // Get the category
    PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
    Category category = passwordsDatabase.getCategory(categoryName);
    // Delete it
    logger.info("Deleting the category " + categoryName + " : " + category);
    // Registering the category in our action tracker
    App.getActionTracker().addCatRemoval(category);
    // Removing it from the database
    passwordsDatabase.removeCategory(category);
    // Remove the description display
    catDescription.clear();
    catDescription.setPrefHeight(0);
    passwordsTable.getItems().clear();
    passwordsTable.setPrefHeight(0);
    summonNotificationPopup(window, "Category " + categoryName + " deleted");
  }

  /** Moves the selected category up in the list */
  public void moveCategoryUp() {
    PasswordsDatabase database = App.getPasswordsDatabase();
    String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
    if (selectedCategory == null) {
      return;
    }
    Category category = database.getCategory(selectedCategory);
    database.moveCategoryUp(category);
    displayCategories();
  }

  /** Moves the selected category down in the list */
  public void moveCategoryDown() {
    PasswordsDatabase database = App.getPasswordsDatabase();
    String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
    if (selectedCategory == null) {
      return;
    }
    Category category = database.getCategory(selectedCategory);
    database.moveCategoryDown(category);
    displayCategories();
  }

  /**
   * Deletes the given password entry from the database
   *
   * @param pwdEntry
   */
  private void deletePasswordEntry(PasswordEntry pwdEntry) {
    PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
    String categoryName = this.categoriesList.getSelectionModel().getSelectedItem();
    Category category = passwordsDatabase.getCategory(categoryName);
    // Delete it
    logger.info("Deleting the password entry " + pwdEntry.getName() + " : " + pwdEntry);
    // Register the action in our action tracker
    App.getActionTracker().addPwdRemoval(pwdEntry, categoryName);
    logger.debug("action tracker: " + App.getActionTracker());
    category.removePasswordEntry(pwdEntry);
    // Refresh the passwords display for that category
    displayPasswords(category);
  }

  /**
   * Moves a password entry from one category to another
   *
   * @param pwdEntry
   * @param oldCatName : old category name, from where we take it
   * @param newCatName : new category name, in where we put it
   */
  private void movePasswordEntry(PasswordEntry pwdEntry, String oldCatName, String newCatName) {
    PasswordsDatabase database = App.getPasswordsDatabase();
    Category oldCat = database.getCategory(oldCatName);
    Category newCat = database.getCategory(newCatName);

    try {
      database.movePasswordEntry(pwdEntry, oldCat, newCat);
      App.getActionTracker().addPwdMovement(pwdEntry, oldCatName, newCatName);
      logger.debug("action tracker: " + App.getActionTracker());
      summonNotificationPopup(this.window, "The password has been moved to " + newCatName);
      logger.info(
          "Moved the password entry : "
              + pwdEntry.getName()
              + " from "
              + oldCatName
              + " to "
              + newCatName);
      if (categoriesList.getSelectionModel().getSelectedItem() != null) {
        displayPasswords(oldCat);
      }
    } catch (IllegalArgumentException ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  private void copyPwdToClipBoard(String password) {
    DesktopUtils.copyToClipBoard(password);
    summonNotificationPopup(this.window, "Password copied to the clipboard");
  }

  private void copyIdToClipBoard(String identifier) {
    DesktopUtils.copyToClipBoard(identifier);
    summonNotificationPopup(this.window, "Identifier copied to the clipboard");
  }

  // endregion

  // region -----Menu Bar-----

  // Used for the fxml file
  public void openCatWindow() {
    removeNotificationPopup();
    openCategoryWindow(null);
  }

  // Used for the fxml file
  public void openPwdWindow() {
    removeNotificationPopup();
    String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
    if (selectedCategory == null) {
      summonNotificationPopup(window, "You must select a category first");
      return;
    }
    openPasswordWindow(null);
  }

  public void saveDatabase() {
    try {
      DatabaseHandler.saveDatabase();
      summonNotificationPopup(window, "Database successfully saved");
    } catch (Exception ex) {
      logger.error(
          "An unexpected error occured while trying to save the passwords database: "
              + ex.getMessage());
    }
  }

  public void resetMasterPassword() {
    App.addTempVariable("master_password_reset_signal", true);
    try {
      Map<String, Object> setupWindowSceneAndController = FXMLUtils.getSceneAndController("setupWindow");
      ((SetupWindowController) (setupWindowSceneAndController.get("Controller"))).displayMinimizeAndCloseButtons();
      FXMLUtils.launchDialogWindow(window, (Scene) (setupWindowSceneAndController.get("Scene")));
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  public void defaultThemeSelected() {
    App.saveProperty(UserSettings.THEME.toString(), "default");
    loadTheme();
  }

  public void lightThemeSelected() {
    App.saveProperty(UserSettings.THEME.toString(), "light");
    loadTheme();
  }

  @SuppressWarnings("unchecked")
  public void togglePwdsVisibilitySetting() {
    // Save the new property value
    var pwdVisibilityPropertyKey = UserSettings.PASSWORDS_VISIBLE.toString();
    var currentVisibility = Boolean.valueOf(appProperties.getProperty(pwdVisibilityPropertyKey));
    App.saveProperty(pwdVisibilityPropertyKey, String.valueOf(!currentVisibility));

    // Refresh the column
    var tableColumns = passwordsTable.getColumns();
    var currentPasswordColumn = tableColumns.stream()
        .filter(col -> "Password".equals(col.getText()))
        .findFirst()
        .get();
    var index = tableColumns.indexOf(currentPasswordColumn);
    var newPasswordColumn = ConstructPasswordColumn();
    tableColumns.set(index, newPasswordColumn);

  }

  public void openReadMe() {
    final String READ_ME_URL = "https://github.com/3h-coder/celatus/blob/main/README.md";
    App.getHS().showDocument(READ_ME_URL);
  }

  public void openUserGuide() {
    final String USER_GUIDE_URL = "https://github.com/3h-coder/celatus/blob/main/docs/User%20Guide.MD";
    App.getHS().showDocument(USER_GUIDE_URL);
  }

  public void openReleaseNotes() {
    final String USER_GUIDE_URL = "https://github.com/3h-coder/celatus/blob/main/docs/Release%20Notes.MD";
    App.getHS().showDocument(USER_GUIDE_URL);
  }

  // endregion

  // endregion

}
