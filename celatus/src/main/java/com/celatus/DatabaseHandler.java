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
    private static final String hintword = "celatus";

    private static Map<String, Object> data;
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
        if (data == null) {
            logger.info("Creating the passwords database file.");
            rawData = hintword;
            CryptoUtils.encryptIntoFile(dbFilePath, rawData, App.getKey(), CryptoUtils.generateIV());
        } else {
            logger.info("Saving the passwords database file.");
            rawData = hintword + "\n" + MapUtils.mapToJson(data, true);
            CryptoUtils.encryptIntoFile(dbFilePath, rawData, App.getKey(), CryptoUtils.generateIV());
        }
    }

    public static void getRawDataFromDatabase() {
        if (dbFileExists()) {
            rawData = CryptoUtils.decryptFile(dbFilePath, App.getKey());
        }
    }

    // endregion
}
