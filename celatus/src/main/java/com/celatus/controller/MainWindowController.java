package com.celatus.controller;

import java.util.Map;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.DatabaseHandler;
import com.celatus.PasswordsDatabase;
import com.celatus.ResizeHelper;
import com.celatus.util.FXMLUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.MenuBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


public class MainWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private AnchorPane columnPane1;
    @FXML
    private AnchorPane descriptionPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label catDescriptionLabel;
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

    private void performGraphicalSetup() {
        // We set the minimum window dimensions
        window.setMinWidth(900);
        window.setMinHeight(600);
        // We make the window resizable
        ResizeHelper.addResizeListener(window);
        // We set the proper bindings
        // catDescriptionLabel.prefWidthProperty().bind(descriptionPane.widthProperty());
        // descriptionPane.prefWidthProperty().bind(catDescriptionLabel.widthProperty());
        // We add our listeners
        catDescriptionLabel.widthProperty().addListener((observable, oldValue, newValue) -> {
            FXMLUtils.adjustLabelHeight(catDescriptionLabel);
        });
        // We fill up the categories list view and set up the context menus
        for (Category category : App.getPasswordsDatabase().getCategories()) {
            FXMLUtils.addToListView(categoriesList, category.getName());
        }
        setContextMenus();
        
    }

    public void test() {

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
            App.warn(window, "Unsaved changes detected, would you like to save these changes?", logger, PopupMode.YES_AND_NO);
        }
        if (App.getSignal("yes_signal") == true) {
            saveDatabase();
            super.close();
        } else if (App.getSignal("no_signal") == true) {
            super.close();
        }    
    }

    @Override
    public void maximize() {
        super.maximize();
        FXMLUtils.adjustLabelHeight(catDescriptionLabel);
    }

    // endregion

    // region =====Back end / Menu Item Methods=====

    private void setContextMenus() {
        // Setting up the context menu for our categories
        categoriesList.setCellFactory( (listView) -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());

            MenuItem editMenuItem = new MenuItem("Edit");
            editMenuItem.setOnAction(event -> {
                String categoryName = cell.getItem();
                Category category = App.getPasswordsDatabase().getCategory(categoryName);
                openCategoryWindow(category);
            });

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event -> {
                App.warn(window, "This is an irreversible action, and you will lose all the passwords in this category, are you sure you want to delete it?",
                        logger, PopupMode.YES_AND_NO);
                if (App.getSignal("yes_signal") == true) {
                    String categoryName = cell.getItem();
                    FXMLUtils.removeFromListView(listView, categoryName);
                    deleteCategory(categoryName);
                }  
            });

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
            cell.setContextMenu(contextMenu);

            cell.setOnMouseClicked(event -> {
                String categoryName = cell.getItem();
                Category category = App.getPasswordsDatabase().getCategory(categoryName);
                String catDescription = category.getDescription();
                AnchorPane descriptionPane = (AnchorPane) window.getScene().lookup("#descriptionPane");
                if (catDescription != null && !catDescription.isEmpty()) {
                    descriptionPane.setVisible(true);
                    catDescriptionLabel.setText(category.getDescription());
                    FXMLUtils.adjustLabelHeight(catDescriptionLabel);
                } else {
                    descriptionPane.setVisible(false);
                }     
            });
            
            return cell;
            }
        );
        // Setting up the context menu for our categories column pane
        ContextMenu contextMenu = new ContextMenu();
        MenuItem newCatMenuItem = new MenuItem("New Category");
        newCatMenuItem.setOnAction(event -> {
            openCategoryWindow(null);
        });
        contextMenu.getItems().add(newCatMenuItem);
        columnPane1.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            contextMenu.show(columnPane1, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        columnPane1.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            contextMenu.hide();
        });
        categoriesList.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            contextMenu.hide();
        });
    }

    public void saveDatabase() {
        try {
            DatabaseHandler.saveDatabase();
        } catch (Exception ex) {
            logger.error("An unexpected error occured while trying to save the passwords database: " + ex.getMessage());
        }
    }  
    
    private void openCategoryWindow(Category category) {
        try{
            // FXMLUtils.launchDialogWindow(window, "categoryWindow");
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
            App.error(this.window, "An error occured: " + ex, logger, PopupMode.OK);
        }  
    }

    private void deleteCategory(String categoryName) {
        PasswordsDatabase passwordsDatabase = App.getPasswordsDatabase();
        Category category = passwordsDatabase.getCategory(categoryName);
        logger.info("Deleting the category " + categoryName + " : " + category);
        passwordsDatabase.removeCategory(category);
    }

    // endregion

}