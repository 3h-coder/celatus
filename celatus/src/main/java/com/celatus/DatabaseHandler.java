package com.celatus;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.controller.PopupMode;
import com.celatus.util.CryptoUtils;

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

    /**
     * Saves the current password database into the password database file.
     */
    public static void saveDatabase() {
        rawData = App.getPasswordsDatabase().toRawData();
        App.setOriginalDatabaseHash(App.getPasswordsDatabase().hashCode());

        if (!dbFileExists()) {
            logger.info("Creating the passwords database file.");
            dbToEncryptedFile(false);
            return;  
        } 

        logger.info("Saving the passwords database.");
        dbToEncryptedFile(true);       
    }

    private static void dbToEncryptedFile(boolean fileExists) {
        try {
            if (fileExists) {
                CryptoUtils.unhideFile(dbFilePath);
            }
            // logger.debug("Saved data: " + rawData);
            CryptoUtils.encryptIntoFile(dbFilePath, rawData, App.getKey(), CryptoUtils.generateIV());
        } catch (Exception ex) {
            App.error(App.getWindow(), "Could not properly save the passwords data: " + ex, logger, PopupMode.OK);
        }
    }

    /**
     * Decrypts the database to save the decrypted string into the rawData variable
     */
    public static void parseRawDataFromDatabase() {
        if (dbFileExists()) {
            try {
                rawData = CryptoUtils.decryptFile(dbFilePath, App.getKey());
            } catch (Exception ex) {
                rawData = null;
            }
        }
    }

    // endregion
}
