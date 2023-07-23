package com.celatus;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseHandler {


    
    // region =====Variables=====

    private static final Logger logger = LogManager.getLogger(DatabaseHandler.class.getName());

    private static final String dbFilePath = "passwords.clts";

    private static String rawData;

    // endregion

    // region =====Getters and Setters=====

    public static String getRawData() {
        return rawData;
    }

    // endregion

    // region =====Static Methods=====

    /**
     * Checks if the passwords database file exists in the app's directory
     * @return
     */
    public static boolean dbFileExists() {
        File file = new File(dbFilePath);
        return file.exists();
    }

    /**
     * Sets the database file as hidden
     * @throws IOException
     */
    public static void concealDatabase() throws IOException {
        if (dbFileExists()) {
            CryptoUtils.hideFile(dbFilePath, true);
        }
    }

    public static void saveDatabase() {
        if (!dbFileExists()) {
            logger.info("Creating the passwords database file.");
            CryptoUtils.encryptIntoFile(dbFilePath, "celatus", App.getKey(), CryptoUtils.generateIV());
        } else {
            logger.info("Saving the passwords database file.");
            try {
                CryptoUtils.unhideFile(dbFilePath);
                logger.debug("Saved raw data: " + rawData);
                CryptoUtils.encryptIntoFile(dbFilePath, rawData, App.getKey(), CryptoUtils.generateIV());
            } catch (Exception ex) {
                App.error(App.getWindow(), "Could not properly save the passwords data", logger);
            }      
        }
    }

    public static void parseRawDataFromDatabase() {
        if (dbFileExists()) {
            rawData = CryptoUtils.decryptFile(dbFilePath, App.getKey());
        }
    }

    // endregion
}
