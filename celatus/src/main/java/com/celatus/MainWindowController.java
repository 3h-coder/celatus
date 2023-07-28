package com.celatus;

import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.MenuBar;
import javafx.scene.control.ListView;

public class MainWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView categoriesList;

    // endregion

    // region =====Window Methods=====

    @Override
    public void initialize() {
        Platform.runLater(() -> {
            for (Category category : App.getPasswordsDatabase().getCategories()) {
                FXMLUtils.addToListView(categoriesList, category.getName());
            }
        });
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
        try {
            DatabaseHandler.saveDatabase();
        } catch (Exception ex) {
            logger.error("An unexpected error occured while trying to save the passwords database: " + ex.getMessage());
        }
        super.close();
    }

    public void saveDatabase() {
        try {
            DatabaseHandler.saveDatabase();
        } catch (Exception ex) {
            logger.error("An unexpected error occured while trying to save the passwords database: " + ex.getMessage());
        }
    }

    // endregion

}