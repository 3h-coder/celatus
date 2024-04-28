package com.celatus.handler;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.util.CryptoUtils;

/** Performs all the actions related to the database file */
public class DatabaseHandler {

  // region =====Variables=====

  public static final String DB_FILE_NAME = "passwords.clts";

  private static final Logger logger = LogManager.getLogger(DatabaseHandler.class.getName());

  private static final String DB_FILE_PATH = App.DIRECTORY + File.separator + DB_FILE_NAME;

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
   *
   * @return
   */
  public static boolean dbFileExists() {
    File file = new File(DB_FILE_PATH);
    return file.exists();
  }

  /**
   * Sets the database file as hidden
   *
   * @throws IOException
   */
  public static void concealDatabase() throws IOException {
    if (dbFileExists()) {
      CryptoUtils.hideFile(DB_FILE_PATH, true);
    }
  }

  /** Saves the current password database into the password database file. */
  public static void saveDatabase() {
    rawData = App.getPasswordsDatabase().toRawData();
    App.setOriginalDatabaseHash(App.getPasswordsDatabase().hashCode());
    App.getActionTracker().clear();

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
        CryptoUtils.unhideFile(DB_FILE_PATH);
      }
      // logger.debug("Saved data: " + rawData);
      CryptoUtils.encryptIntoFile(DB_FILE_PATH, rawData, App.getKey(), CryptoUtils.generateIV());
    } catch (Exception ex) {
      App.error(
          App.getWindow(),
          ex,
          "Could not properly save the passwords data",
          logger,
          AlertMode.OK,
          true);
    }
  }

  /**
   * Decrypts the database to save the decrypted string into the rawData variable
   */
  public static void parseRawDataFromDatabase() {
    if (dbFileExists()) {
      try {
        rawData = CryptoUtils.decryptFile(DB_FILE_PATH, App.getKey());
      } catch (Exception ex) {
        rawData = null;
      }
    }
  }

  // endregion
}
