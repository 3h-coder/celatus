package com.celatus.controller;

import java.time.LocalDateTime;
import java.util.Map;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordEntry;
import com.celatus.DatabaseHandler;
import com.celatus.PasswordsDatabase;
import com.celatus.ResizeHelper;
import com.celatus.util.CustomDateUtils;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.MenuBar;

import org.apache.commons.lang3.StringUtils;

/**
 * The controller of the App's main window
 */
public class MainWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private AnchorPane columnPane1;
    @FXML
    private SplitPane columnPane2;
    @FXML
    private AnchorPane descriptionPane;
    @FXML
    private AnchorPane passwordsPane;
    @FXML
    private AnchorPane blankSpacePane;
    @FXML
    private TextArea catDescription;
    @FXML
    private TableView<PasswordEntry> passwordsTable;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView<String> categoriesList;

    // endregion

    // region =====Window Methods=====

    @Override
    public void initialize() {

        super.initialize();
        Platform.runLater(() -> {
            performGraphicalSetup();
        });
        // logger.debug(App.getPasswordsDatabase());    
    }

    /**
     * Performs all the graphical setup such as the window dimensions or the context menus
     */
    private void performGraphicalSetup() {
        // We set the minimum window dimensions
        window.setMinWidth(900);
        window.setMinHeight(600);
        // We make the window resizable
        ResizeHelper.addResizeListener(window);
        // We set the proper bindings
        descriptionPane.maxHeightProperty().bind(catDescription.prefHeightProperty());
        descriptionPane.minHeightProperty().bind(catDescription.prefHeightProperty());
        passwordsPane.maxHeightProperty().bind(passwordsTable.prefHeightProperty());
        passwordsPane.minHeightProperty().bind(passwordsTable.prefHeightProperty());
        // We add our listeners
        catDescription.widthProperty().addListener((observable, oldValue, newValue) -> {
            FXMLUtils.adjustTextAreaHeight(catDescription);
        });
        // We disable the split pane divider
        columnPane2.lookupAll(".split-pane-divider").stream().forEach(div ->  div.setMouseTransparent(true) );
        // We fill up the categories list view and set up the context menus
        fillCategories();
        // We set our context menus and passwords table
        setContextMenus();
        setPasswordsTable();
    }

    public void test() {
        logger.debug("test");
    }

    @FXML
    public void showDescription(String description) {
        descriptionPane.setVisible(true);
        catDescription.setText(description);
        FXMLUtils.adjustTextAreaHeight(catDescription);
    }

    private void fillCategories() {
        for (Category category : App.getPasswordsDatabase().getCategories()) {
            FXMLUtils.addToListView(categoriesList, category.getName());
        }
    }

    private void fillPasswordsTable(Category cat) {
        for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
            FXMLUtils.addToTableView(passwordsTable, pwdEntry);
        }
    }

    /**
     * Displays all the password entries of the selected category, refreshing the view
     * @param category
     */
    public void displayPasswords(Category category) {
        passwordsTable.getItems().clear();
        passwordsTable.setPrefHeight(0);
        if (category.getPasswordEntries() != null) {
            passwordsTable.setVisible(true);
            fillPasswordsTable(category); 
        } else {
            passwordsTable.setVisible(false);
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
            App.warn(window, "Unsaved changes detected, would you like to save these changes?", logger, AlertMode.YES_AND_NO);
        }
        if (App.getSignal("yes_signal")) {
            saveDatabase();
            super.close();
        } else if (App.getSignal("no_signal")) {
            super.close();
        }    
    }

    @Override
    public void maximize() {
        super.maximize();
        if (StringUtils.isBlank(catDescription.getText())) {
            catDescription.setPrefHeight(0);
        }
        else {
            FXMLUtils.adjustTextAreaHeight(catDescription);
        }
    }

    // endregion

    // region =====Back end / Menu Item Methods=====

    // region -----Categories and Password Entries-----

    /**
     * Sets up the context menus for our categories and password entries
     */
    private void setContextMenus() {
        // Setting up the context menu for our categories
        setCatListViewContextMenu();
        // Setting up the context menu for our categories column pane
        setCatPaneContextMenu();
        // Setting up the context menu for our passwords row in our passwords table
        setPwdTableContextMenu();
        // Setting up the context menu for our passwords column pane
        setPwdPaneContextMenu();
    }

    /**
     * Sets the context menu for our passwords pane
     */
    private void setPwdPaneContextMenu() {
        ContextMenu newPwdContextMenu = new ContextMenu();
        MenuItem newPwdMenuItem = new MenuItem("new password");
        newPwdMenuItem.setOnAction(event -> {
            openPasswordWindow(null);
        });
        newPwdContextMenu.getItems().add(newPwdMenuItem);
        blankSpacePane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
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
        columnPane2.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            newPwdContextMenu.hide();
        });
        passwordsTable.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            newPwdContextMenu.hide();
        });
    }

    /**
     * Sets the context menu for our password entries table
     */
    private void setPwdTableContextMenu() {
        

        ContextMenu pwdContextMenu = new ContextMenu();
        MenuItem editPwdMenuItem = new MenuItem("edit");
        MenuItem deletePwdMenuItem = new MenuItem("delete");
        Menu movePwdMenuItem = new Menu("move to");

        editPwdMenuItem.setOnAction(event -> {
            PasswordEntry selectedPassword = passwordsTable.getSelectionModel().getSelectedItem();
            openPasswordWindow(selectedPassword);
        });

        deletePwdMenuItem.setOnAction(event -> {
            App.warn(window, "This is an irreversible action, are you sure you want to delete it?",
                        logger, AlertMode.YES_AND_NO);
            if (App.getSignal("yes_signal") == true) {
                PasswordEntry selectedPassword = passwordsTable.getSelectionModel().getSelectedItem();
                deletePasswordEntry(selectedPassword);
            } 
        });

        
        pwdContextMenu.getItems().addAll(editPwdMenuItem, deletePwdMenuItem, movePwdMenuItem);
        // Adding all of our categories to the "move to" menu, (except the selected one). This has to be done during runtime
        this.passwordsTable.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            movePwdMenuItem.getItems().clear();
            String selectedCategory = categoriesList.getSelectionModel().getSelectedItem();
            PasswordEntry selectedPassword = passwordsTable.getSelectionModel().getSelectedItem();
            for (Category category : App.getPasswordsDatabase().getCategories()) {
                String categoryName = category.getName();
                if (StringUtils.equals(selectedCategory, categoryName)) {
                    continue;
                }
                MenuItem menuItem = new MenuItem(categoryName);
                menuItem.setOnAction(_event -> movePasswordEntry(selectedPassword, selectedCategory, categoryName));
                movePwdMenuItem.getItems().add(menuItem);
            }
        });
        this.passwordsTable.setContextMenu(pwdContextMenu);
    }

    /**
     * Sets the context menu of our categories pane
     */
    private void setCatPaneContextMenu() {
        ContextMenu newCatContextMenu = new ContextMenu();
        MenuItem newCatMenuItem = new MenuItem("new category");
        newCatMenuItem.setOnAction(event -> {
            openCategoryWindow(null);
        });
        newCatContextMenu.getItems().add(newCatMenuItem);
        columnPane1.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            newCatContextMenu.show(columnPane1, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        columnPane1.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            newCatContextMenu.hide();
        });
        categoriesList.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            newCatContextMenu.hide();
        });
    }

    /**
     * Sets the context menu for our categories list view
     */
    private void setCatListViewContextMenu() {
        this.categoriesList.setCellFactory( (listView) -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());

            MenuItem editMenuItem = new MenuItem("edit");
            MenuItem deleteMenuItem = new MenuItem("delete");

            editMenuItem.setOnAction(event -> {
                String categoryName = cell.getItem();
                Category category = App.getPasswordsDatabase().getCategory(categoryName);
                openCategoryWindow(category);
            });
            
            deleteMenuItem.setOnAction(event -> {
                App.warn(window, "This is an irreversible action, and you will lose all the passwords in this category, are you sure you want to delete it?",
                        logger, AlertMode.YES_AND_NO);
                if (App.getSignal("yes_signal") == true) {
                    String categoryName = cell.getItem();
                    FXMLUtils.removeFromListView(listView, categoryName);
                    deleteCategory(categoryName);
                    this.categoriesList.getSelectionModel().clearSelection();
                }  
            });

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
            cell.setContextMenu(contextMenu);

            cell.setOnMouseClicked(event -> {
                String categoryName = cell.getItem();
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
            });
            
            return cell;
            }
        );
    }

    /**
     * Sets the password entries table
     */
    @SuppressWarnings("unchecked")
    private void setPasswordsTable() {
        TableColumn nameColumn = new TableColumn("Name");
        TableColumn identifierColumn = new TableColumn("Identifier");
        TableColumn passwordColumn = new TableColumn("Password");
        TableColumn lastEditedColumn = new TableColumn("Last edited");

        FXMLUtils.setMinWidth(100.0, nameColumn, identifierColumn, passwordColumn, lastEditedColumn);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        lastEditedColumn.setCellValueFactory(new PropertyValueFactory<>("lastEditDate"));

        passwordColumn.setCellFactory(column -> {
            return new TableCell<PasswordEntry, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText("\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022");
                    }
                }
            };
        });

        lastEditedColumn.setCellFactory(column -> {
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

        this.passwordsTable.getColumns().addAll(nameColumn, identifierColumn, passwordColumn, lastEditedColumn);
    }

    /**
     * Opens a window to create a category or edit the selected category
     * @param category
     */
    private void openCategoryWindow(Category category) {
        try{
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
            controller.setCategory(App.getPasswordsDatabase().getCategory(this.categoriesList.getSelectionModel().getSelectedItem()));
            controller.setTitle(title);
            FXMLUtils.launchDialogWindow(this.window, scene);
        } catch (Exception ex) {
            App.error(this.window, ex,"An error occured", logger, AlertMode.OK, true);
        }  
    }

    /**
     * Deletes a given category from the database
     * @param categoryName : Name of the category to be deleted
     */
    private void deleteCategory(String categoryName) {
        // Get the category
        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
        Category category = passwordsDatabase.getCategory(categoryName);
        // Delete it
        logger.info("Deleting the category " + categoryName + " : " + category);
        passwordsDatabase.removeCategory(category);
        // Remove the description display
        catDescription.clear();
        catDescription.setPrefHeight(0);
        passwordsTable.getItems().clear();
        passwordsTable.setPrefHeight(0);
        FXMLUtils.summonPopup(window, "Category " + categoryName + " deleted");
    }

    /**
     * Deletes the given password entry from the database
     * @param pwdEntry
     */
    private void deletePasswordEntry(PasswordEntry pwdEntry) {
        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
        String categoryName = this.categoriesList.getSelectionModel().getSelectedItem();
        Category category = passwordsDatabase.getCategory(categoryName);
        // Delete it
        logger.info("Deleting the password entry " + pwdEntry.getName() + " : " + pwdEntry);
        category.removePasswordEntry(pwdEntry);
        // Refresh the passwords display for that category
        displayPasswords(category);
    }

    /**
     * Moves a password entry from one category to another
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
            FXMLUtils.summonPopup(this.window, "The password has been moved to " + newCatName);
            logger.info("Moved the password entry : " + pwdEntry.getName() + " from " + oldCatName + " to " + newCatName);
            displayPasswords(oldCat);
        } catch (IllegalArgumentException ex) {
            App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
        } 
    }

    // endregion

    // region -----Menu Bar-----

    public void saveDatabase() {
        try {
            DatabaseHandler.saveDatabase();
            FXMLUtils.summonPopup(window, "Database successfully saved");
        } catch (Exception ex) {
            logger.error("An unexpected error occured while trying to save the passwords database: " + ex.getMessage());
        }
    }  

    public void resetMasterPassword() {
        App.addTempVariable("master_password_reset_signal", true);
        try {
            FXMLUtils.launchDialogWindow(window, "setupWindow");
        } catch (Exception ex) {
            App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
        }
    }

    // endregion
    
    // endregion

}