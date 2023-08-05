package com.celatus.controller;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.DatabaseHandler;

import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;

import javafx.scene.control.MenuBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.control.ContextMenu;
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
    private MenuBar menuBar;
    @FXML
    private ListView<String> categoriesList;

    // endregion

    // region =====Window Methods=====

    @Override
    public void initialize() {

        for (Category category : App.getPasswordsDatabase().getCategories()) {
            FXMLUtils.addToListView(categoriesList, category.getName());
        }
        setContextMenus();
        logger.debug(App.getPasswordsDatabase());
        super.initialize();
    }

    public void test() {
        logger.debug("Clicking on the anchor");
    }

    // endregion

    // region =====Event Methods=====

    @Override
    public void close() {
        saveDatabase();
        super.close();
    }

    // endregion

    // region =====Back end / Menu Item Methods=====

    private void setContextMenus() {
        // Setting up the context menu for our categories
        categoriesList.setCellFactory( (listView) -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            MenuItem menuItem1 = new MenuItem("Edit");
            MenuItem menuItem2 = new MenuItem("Delete");
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(menuItem1, menuItem2);
            cell.setContextMenu(contextMenu);
            return cell;
            }
        );
        // Setting up the context menu for our categories column pane
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("New Category");
        menuItem1.setOnAction(event -> {
            openCategoryWindow();
        });
        contextMenu.getItems().add(menuItem1);
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
    
    private void openCategoryWindow() {
        try{
            App.launchDialogWindow(window, "categoryWindow");
        } catch (Exception ex) {
            App.error(window, "An error occured: " + ex, logger);
        }
        
    }

    // endregion

}