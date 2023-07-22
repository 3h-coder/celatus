package com.celatus;

import javafx.fxml.FXML;

import javafx.scene.control.MenuBar;

public class MainWindowController extends BaseWindowController {

    // region =====Variables=====

    @FXML
    private MenuBar menuBar;

    // endregion

    // region =====Window Methods=====

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