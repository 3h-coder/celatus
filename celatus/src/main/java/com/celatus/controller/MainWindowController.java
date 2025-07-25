package com.celatus.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.constants.Constants;
import com.celatus.enums.AlertMode;
import com.celatus.enums.AppTempVariable;
import com.celatus.enums.AppTheme;
import com.celatus.enums.UserSettings;
import com.celatus.enums.WindowType;
import com.celatus.handler.DatabaseHandler;
import com.celatus.handler.SearchHandler;
import com.celatus.models.Category;
import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.DesktopUtils;
import com.celatus.util.FXMLUtils;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
  @FXML
  private Label addPwdLabel;
  @FXML
  private BorderPane logoBorderPane;
  @FXML
  private ImageView backgroundLogo;

  // endregion

  // region =====Window Methods=====

  @Override
  protected void lateInitialize() {
    super.lateInitialize();
    performGraphicalSetup();
    categoriesList.requestFocus();
  }

  /**
   * Performs all the graphical setup such as the window dimensions or the context
   * menus
   */
  private void performGraphicalSetup() {
    // We set the minimum window dimensions
    window.setMinWidth(700);
    window.setMinHeight(400);

    setBindings();
    addFocusListeners();
    addCategoryListeners();
    addEventFilters();

    // We fill up the categories list view and set up the context menus
    refreshCategories();
    // We set our context menus and passwords table
    setContextMenus();
    setPasswordsTable();
  }

  private void setBindings() {
    descriptionPane.maxHeightProperty().bind(catDescription.prefHeightProperty());
    descriptionPane.minHeightProperty().bind(catDescription.prefHeightProperty());
    passwordsPane.maxHeightProperty().bind(columnPane2.heightProperty().multiply(0.8));
    backgroundLogo.fitHeightProperty().bind(Bindings.min(400, logoBorderPane.heightProperty().multiply(0.6)));
    backgroundLogo.fitWidthProperty().bind(backgroundLogo.fitHeightProperty());
  }

  private void addFocusListeners() {
    scene.focusOwnerProperty().addListener((observable, oldFocus, newFocus) -> {
      if (newFocus == null) {
        return;
      }

      // Hiding the context menus on focus lost
      if (oldFocus == categoriesList) {
        for (var cell : FXMLUtils.getAllNodesByClass(categoriesList, ListCell.class)) {
          var contextMenu = cell.getContextMenu();
          if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
          }
        }
      } else if (oldFocus == passwordsTable) {
        var contextMenu = passwordsTable.getContextMenu();
        if (contextMenu != null && contextMenu.isShowing()) {
          contextMenu.hide();
        }
      }

      // Select the first category when the categories list gets the focus
      // and no category is selected
      if (newFocus == categoriesList && getSelectedCategory() == null) {
        categoriesList.getSelectionModel().select(0);
      }
      // Select the first password entry when the passwords table gets the focus
      // and no password entry is selected
      else if (newFocus == passwordsTable && getSelectedPassword() == null) {
        passwordsTable.getSelectionModel().select(0);
      }
    });
  }

  private void addCategoryListeners() {
    categoriesList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (StringUtils.isBlank(newValue)) {
                addPwdLabel.setVisible(false);
                return;
              }
              addPwdLabel.setVisible(true);
              showCategory(newValue);
            });

    catDescription
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String selectedCategory = getSelectedCategory();
              if (selectedCategory != null) {
                FXMLUtils.adjustTextAreaHeight(catDescription);
              }
            });
  }

  private void addEventFilters() {
    addCategoriesListEventFilters();
    addPasswordsTableEventFilter();
  }

  private void addCategoriesListEventFilters() {
    categoriesList.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();
      // Go to the search bar when pressing key up from the first category
      if (eventCode == KeyCode.UP && categoriesList.getSelectionModel().getSelectedIndex() == 0) {
        searchBar.requestFocus();
        return;
      }

      // Go back to the first category from a key down on the last one
      if (eventCode == KeyCode.DOWN &&
          categoriesList.getSelectionModel().getSelectedIndex() == categoriesList.getItems().size() - 1) {
        categoriesList.getSelectionModel().select(0);
        event.consume();
        return;
      }

    });
  }

  private void addPasswordsTableEventFilter() {
    passwordsTable.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      var eventCode = event.getCode();

      if (eventCode == KeyCode.LEFT) {
        categoriesList.requestFocus();
        passwordsTable.getSelectionModel().clearSelection();
      }

    });
  }

  /**
   * Displays the category's description as well as all of its passwords
   */
  private void showCategory(String categoryName) {
    Category category = App.getPasswordsDatabase().getCategory(categoryName);
    String categoryDescription = category.getDescription();
    if (StringUtils.isNotBlank(categoryDescription)) {
      showDescription(categoryDescription);
    } else {
      catDescription.clear();
      catDescription.setPrefHeight(0);
    }
    // Displaying all the password entries
    refreshPasswords(category);
  }

  /**
   * Displays the given description in the description pane
   */
  @FXML
  public void showDescription(String description) {
    descriptionPane.setVisible(true);
    catDescription.setText(description);
    FXMLUtils.adjustTextAreaHeight(catDescription);
  }

  /**
   * Displays all the categories of our database, refreshing the view
   * and selecting the first category if none is selected
   */
  private void refreshCategories() {
    String selectedCategory = getSelectedCategory();
    // refresh
    categoriesList.getItems().clear();
    var passwordsDatabase = App.getPasswordsDatabase();
    for (Category category : passwordsDatabase.getCategories()) {
      FXMLUtils.addToListView(categoriesList, category.getName());
    }

    if (categoriesList.getItems().isEmpty()) {
      return;
    }

    // reselect the previously selected category (if it still exists)
    if (selectedCategory != null && passwordsDatabase.hasCategory(selectedCategory)) {
      categoriesList.getSelectionModel().select(selectedCategory);
    } else {
      // select the first category if none is selected
      categoriesList.getSelectionModel().selectFirst();
    }
  }

  /**
   * Displays all the password entries of the selected category, refreshing the
   * view
   */
  public void refreshPasswords(Category category) {
    passwordsPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

    passwordsTable.getItems().clear();
    passwordsTable.setPrefHeight(0);

    if (category.getPasswordEntries() != null) {
      passwordsTable.setVisible(true);
      fillPasswordsTable(category);
    } else {
      hidePasswordsTable(true);
    }
  }

  /**
   * Displays all the given password entries
   *
   * @param category
   */
  private void refreshPasswordsFromSearch(List<PasswordEntry> passwordEntries) {
    passwordsTable.getItems().clear();
    passwordsTable.setPrefHeight(0);

    catDescription.setPrefHeight(0);

    if (passwordEntries != null && !passwordEntries.isEmpty()) {
      passwordsTable.setVisible(true);
      fillPasswordsTable(passwordEntries);
    } else {
      hidePasswordsTable(false);
    }
  }

  private void hidePasswordsTable(boolean shiftFocus) {
    passwordsTable.setVisible(false);
    // Note: We do not want to shift the focus during a password search, as we want
    // the focus to remain on the search bar.
    if (shiftFocus) {
      // Prevent JavaFX from switching the focus to the next focusable element in the
      // tree (usually the search bar) and explicitly request focus on the categories
      // list.
      categoriesList.requestFocus();
    }
  }

  /**
   * Fills the password table with the given category's password entries
   */
  private void fillPasswordsTable(Category category) {
    for (PasswordEntry pwdEntry : category.getPasswordEntries()) {
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

  private String getSelectedCategory() {
    return categoriesList.getSelectionModel().getSelectedItem();
  }

  private PasswordEntry getSelectedPassword() {
    return passwordsTable.getSelectionModel().getSelectedItem();
  }

  // endregion

  // region =====Event Methods=====

  @Override
  @FXML
  protected void close() {
    checkForUnsavedChanges();
  }

  /**
   * Checks if there are unsaved changes in the database, and prompts the user
   * to save them if there are.
   */
  private void checkForUnsavedChanges() {
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
    if (App.getSignal(AppTempVariable.SIGNAL_YES)) {
      saveDatabase();
      super.close();
    } else if (App.getSignal(AppTempVariable.SIGNAL_NO)) {
      super.close();
    }
  }

  @Override
  @FXML
  protected void windowKeyPressed(KeyEvent event) {
    super.windowKeyPressed(event);
    KeyCode eventCode = event.getCode();
    // Full screen
    if (eventCode == KeyCode.F11) {
      if (window.isMaximized()) {
        window.setMaximized(false);
        controlCatDescription();
      } else {
        window.setMaximized(true);
      }
    } else if (eventCode == KeyCode.ESCAPE && window.isMaximized()) {
      window.setMaximized(false);
      controlCatDescription();
    }
    // Ctrl+Z
    if (event.isControlDown() && eventCode == KeyCode.Z) {
      App.getActionTracker().goBackwards();

      refreshPasswordsAndCategories();
    }
    // Ctrl+Y
    if (event.isControlDown() && eventCode == KeyCode.Y) {
      App.getActionTracker().goForwards();

      refreshPasswordsAndCategories();
    }
  }

  private void refreshPasswordsAndCategories() {
    refreshPasswordsForSelectedCategory();
    refreshCategories();
  }

  private void refreshPasswordsForSelectedCategory() {
    String selectedCategory = getSelectedCategory();
    if (selectedCategory != null) {
      var category = App.getPasswordsDatabase().getCategory(selectedCategory);
      // The category may be null if the ctrl + Z/Y action deleted the selected
      // category
      if (category == null) {
        return;
      }
      refreshPasswords(category);
    }
  }

  @FXML
  private void searchBarKeyPressed(KeyEvent event) {
    KeyCode eventCode = event.getCode();

    if (eventCode == KeyCode.ESCAPE || eventCode == KeyCode.TAB) {
      // Disabling the onKeyTyped other wise it is triggered
      searchBar.setOnKeyTyped(keyTypedEvent -> {
      });
      searchBar.getParent().requestFocus();

      return;
    }

    // Give the focus to the categories list if the user pressed the down arrow
    if (eventCode == KeyCode.DOWN) {
      categoriesList.requestFocus();
      return;
    }

    // Give the focus to the menu bar if the user pressed the up arrow
    if (eventCode == KeyCode.UP) {
      menuBar.requestFocus();
      return;
    }

    // Re-enabling onKeyTyped in case the user previously pressed escape or tab
    searchBar.setOnKeyTyped(keyTypedEvent -> searchBarKeyTyped(keyTypedEvent));
  }

  @FXML
  private void searchBarKeyTyped(KeyEvent event) {
    refreshPasswordsFromSearch(searchPassword());
  }

  @FXML
  private void searchBarOnAction() {
    var searchResult = searchPassword();
    if (searchResult == null) {
      return;
    }
    if (searchResult.isEmpty()) {
      summonNotificationPopup(window, "No password found");
      return;
    }
    refreshPasswordsFromSearch(searchResult);
  }

  @FXML
  private void categoriesListKeyPressed(KeyEvent event) {
    var keyCode = event.getCode();
    var selectedCategory = getSelectedCategory();

    if (keyCode == KeyCode.ENTER && selectedCategory != null) {
      var index = categoriesList.getSelectionModel().getSelectedIndex();
      var cell = FXMLUtils.getAllNodesByClass(categoriesList, ListCell.class).stream()
          .filter(c -> c.getIndex() == index)
          .findFirst().orElse(null);

      if (cell == null) {
        return;
      }
      var localBounds = cell.getBoundsInLocal();
      var screenBounds = cell.localToScreen(localBounds);
      cell.fireEvent(new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
          0, 0, screenBounds.getMinX(), screenBounds.getMaxY(), false, null));
    }
  }

  @FXML
  private void passwordsTableKeyPressed(KeyEvent event) {
    var keyCode = event.getCode();
    var selectedPasswordEntry = getSelectedPassword();

    if (keyCode == KeyCode.ENTER && selectedPasswordEntry != null) {
      var index = passwordsTable.getSelectionModel().getSelectedIndex();
      var row = FXMLUtils.getAllNodesByClass(passwordsTable, TableRow.class).stream()
          .filter(r -> r.getIndex() == index)
          .findFirst().orElse(null);

      if (row == null) {
        return;
      }
      var localBounds = row.getBoundsInLocal();
      var screenBounds = row.localToScreen(localBounds);
      passwordsTable.fireEvent(new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
          0, 0, screenBounds.getMinX(), screenBounds.getMaxY(), false, null));
    }
  }

  /**
   * Searches for the given password, and displays all the passwords that contain
   * its name.
   */
  private List<PasswordEntry> searchPassword() {
    // We deselect the selected category
    categoriesList.getSelectionModel().clearSelection();
    return SearchHandler.searchPassword(searchBar.getText());
  }

  // endregion

  // region =====Menu Item / Context Menu Methods=====

  // region -----Categories and Password Entries Context Menus-----

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
                if (App.getSignal(AppTempVariable.SIGNAL_YES) == true) {
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
          App.warn(window, "Are you sure you want to delete it?", logger, AlertMode.YES_AND_NO);
          if (App.getSignal(AppTempVariable.SIGNAL_YES)) {
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
    passwordsTable.addEventHandler(
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

  /** Sets the password entries table, formatting the columns appropriately. */
  @SuppressWarnings("unchecked")
  private void setPasswordsTable() {

    var nameColumn = new TableColumn<PasswordEntry, String>("Name");
    var identifierColumn = new TableColumn<PasswordEntry, String>("Identifier");
    var lastEditedColumn = new TableColumn<PasswordEntry, LocalDateTime>("Last edited");

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

    var passwordColumn = ConstructPasswordColumn();

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
   */
  private void openCategoryWindow(Category category) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(WindowType.CATEGORY);
      Scene scene = (Scene) map.get(Constants.SCENE);
      CategoryWindowController controller = (CategoryWindowController) map.get(Constants.CONTROLLER);

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
   */
  private void openPasswordWindow(PasswordEntry pwdEntry) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(WindowType.PASSWORD);
      Scene scene = (Scene) map.get(Constants.SCENE);
      PasswordWindowController controller = (PasswordWindowController) map.get(Constants.CONTROLLER);

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
  @FXML
  private void moveCategoryUp() {
    PasswordsDatabase database = App.getPasswordsDatabase();
    String selectedCategory = getSelectedCategory();
    if (selectedCategory == null) {
      return;
    }
    Category category = database.getCategory(selectedCategory);
    database.moveCategoryUp(category);
    refreshCategories();
  }

  /** Moves the selected category down in the list */
  @FXML
  private void moveCategoryDown() {
    PasswordsDatabase database = App.getPasswordsDatabase();
    String selectedCategory = getSelectedCategory();
    if (selectedCategory == null) {
      return;
    }
    Category category = database.getCategory(selectedCategory);
    database.moveCategoryDown(category);
    refreshCategories();
  }

  /**
   * Deletes the given password entry from the database
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
    refreshPasswords(category);
  }

  /**
   * Moves a password entry from one category to another
   *
   * @param pwdEntry   : the password entry to be moved
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
      if (getSelectedCategory() != null) {
        refreshPasswords(oldCat);
      }
    } catch (IllegalArgumentException ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  private void copyPwdToClipBoard(String password) {
    final int clipboardTimeoutInS = 20;

    logger.info("Copying password to clipboard");
    DesktopUtils.copyToClipBoard(password);
    summonNotificationPopup(window, "Password copied to the clipboard (for "
        + clipboardTimeoutInS + " seconds)");

    // We clear the clipboard after a certain timeout
    var pause = new PauseTransition(javafx.util.Duration.seconds(clipboardTimeoutInS));
    pause.setOnFinished(event -> {
      var clipboardContent = DesktopUtils.getClipboardContents();
      if (clipboardContent.isBlank() || !clipboardContent.equals(password)) {
        return;
      }
      logger.info("Clearing the clipboard");
      DesktopUtils.copyToClipBoard(StringUtils.EMPTY);
    });
    pause.play();
  }

  private void copyIdToClipBoard(String identifier) {
    DesktopUtils.copyToClipBoard(identifier);
    summonNotificationPopup(this.window, "Identifier copied to the clipboard");
  }

  // endregion

  // region -----Menu Bar-----

  @FXML
  private void openCatWindow() {
    openCategoryWindow(null);
  }

  @FXML
  private void openPwdWindow() {
    String selectedCategory = getSelectedCategory();
    if (selectedCategory == null) {
      summonNotificationPopup(window, "You must select a category first");
      return;
    }
    openPasswordWindow(null);
  }

  @FXML
  private void saveDatabase() {
    try {
      DatabaseHandler.saveDatabase();
      summonNotificationPopup(window, "Database successfully saved");
    } catch (Exception ex) {
      logger.error(
          "An unexpected error occured while trying to save the passwords database", ex);
    }
  }

  @FXML
  private void resetMasterPassword() {
    App.addTempVariable(AppTempVariable.SIGNAL_MASTER_PASSWORD_RESET, true);
    try {
      Map<String, Object> setupWindowSceneAndController = FXMLUtils.getSceneAndController(WindowType.SETUP);
      var controller = ((SetupWindowController) (setupWindowSceneAndController.get(Constants.CONTROLLER)));
      controller.displayMinimizeAndCloseButtons();
      controller.hidePasswordLocationFields();
      controller.setBorderVisible();
      FXMLUtils.launchDialogWindow(window, (Scene) (setupWindowSceneAndController.get(Constants.SCENE)));
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  @FXML
  private void defaultThemeSelected() {
    App.saveProperty(UserSettings.THEME.toString(), AppTheme.DEFAULT.toString());
    loadTheme();
  }

  @FXML
  private void lightThemeSelected() {
    App.saveProperty(UserSettings.THEME.toString(), AppTheme.LIGHT.toString());
    loadTheme();
  }

  @SuppressWarnings("unchecked")
  @FXML
  private void togglePwdsVisibilitySetting() {
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

  @FXML
  private void changePwdsFileLocation() {
    try {
      Map<String, Object> changePwdsFileLocationSceneAndController = FXMLUtils
          .getSceneAndController(WindowType.CHANGE_PASSWORDS_FILE_LOCATION);
      FXMLUtils.launchDialogWindow(window, (Scene) (changePwdsFileLocationSceneAndController.get(Constants.SCENE)));
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  @FXML
  private void openReadMe() {
    final String READ_ME_URL = "https://github.com/3h-coder/celatus/blob/main/README.md";
    App.getHS().showDocument(READ_ME_URL);
  }

  @FXML
  private void openUserGuide() {
    final String USER_GUIDE_URL = "https://github.com/3h-coder/celatus/blob/main/docs/User%20Guide.MD";
    App.getHS().showDocument(USER_GUIDE_URL);
  }

  @FXML
  private void openReleaseNotes() {
    final String USER_GUIDE_URL = "https://github.com/3h-coder/celatus/blob/main/docs/Release%20Notes.MD";
    App.getHS().showDocument(USER_GUIDE_URL);
  }

  // endregion

  // endregion

}
