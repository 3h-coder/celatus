package com.celatus;

public class MainWindowController extends BaseWindowController {

    // region =====Variables=====

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

    // endregion

}