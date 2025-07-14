package com.celatus.handler;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.enums.UserSettings;
import com.celatus.util.CryptoUtils;
import com.celatus.util.DesktopUtils;

/** Performs all the actions related to the database file */
public class DatabaseHandler {

  // region =====Variables=====

  public static final String DB_FILE_NAME = "passwords.clts";

  private static final Logger logger = LogManager.getLogger(DatabaseHandler.class.getName());

  private static final String pwdFileLocationPropertyKey = UserSettings.PASSWORDS_FILE_LOCATION.toString();

  private static String dbFolderPath;

  private static String rawData;

  // endregion

  // region =====Getters and Setters=====

  public static String getRawData() {
    return rawData;
  }

  /**
   * @return The path to the database folder, which is the current working
   *         directory by default.
   */
  public static String getDBFolderPath() {
    return dbFolderPath != null ? dbFolderPath : App.getProperties().getProperty(pwdFileLocationPropertyKey);
  }

  /**
   * Sets the database folder path to the specified value.
   * 
   * @param dbFolderPath
   * @throws Exception
   */
  public static void setDBFolderPath(String dbFolderPath) throws Exception {
    var previousFolderPath = getDBFolderPath();
    if (previousFolderPath.equals(dbFolderPath)) {
      return; // No change needed
    }

    if (!isFolderValid(dbFolderPath)) {
      throw new IllegalArgumentException("Invalid database folder path: " + dbFolderPath);
    }

    var shouldMoveFile = dbFileExists();
    saveFolderPath(dbFolderPath);

    if (shouldMoveFile) {
      moveDBFile(dbFolderPath, previousFolderPath);
    }
  }

  // endregion

  // region =====Static Methods=====

  public static String getDefaultDBFolderPath() {
    return System.getProperty("user.dir");
  }

  /**
   * Checks if the passwords database file exists in the app's directory
   *
   * @return
   */
  public static boolean dbFileExists() {
    File file = new File(getDBFilePath());
    return file.exists();
  }

  /**
   * Decrypts the database to save the decrypted string into the rawData variable
   */
  public static void parseRawDataFromDatabase() {
    if (dbFileExists()) {
      try {
        rawData = CryptoUtils.decryptFile(getDBFilePath(), App.getKey());
      } catch (Exception ex) {
        rawData = null;
      }
    }
  }

  /**
   * Sets the database file as hidden
   *
   * @throws IOException
   */
  public static void concealDatabase() throws IOException {
    if (dbFileExists()) {
      CryptoUtils.hideFile(DB_FILE_NAME, true);
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

  private static String getDBFilePath() {
    return getDBFolderPath() + File.separator + DB_FILE_NAME;
  }

  private static void dbToEncryptedFile(boolean fileExists) {
    try {
      var dbFilePath = getDBFilePath();
      if (fileExists) {
        CryptoUtils.unhideFile(dbFilePath);
      }
      CryptoUtils.encryptIntoFile(dbFilePath, rawData, App.getKey(), CryptoUtils.generateIV());
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

  private static boolean isFolderValid(String folderPath) {
    File dir = new File(folderPath);
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        logger.error("Failed to create directory: " + folderPath);
        return false;
      }
    }
    if (!dir.isDirectory() || !DesktopUtils.isDirectoryWritable(folderPath)) {
      logger.error("Database folder path is not a valid directory or is not writable: " + folderPath);
      return false;
    }
    return true;
  }

  private static void moveDBFile(String dbFolderPath, String previousFolderPath) throws Exception {
    logger.info("Moving the passwords database file from " + previousFolderPath + " to " + dbFolderPath);
    try {
      var previousFilePath = previousFolderPath + File.separator + DB_FILE_NAME;
      var newFilePath = dbFolderPath + File.separator + DB_FILE_NAME;
      DesktopUtils.moveFile(previousFilePath, newFilePath);
      logger.info("Passwords database file moved successfully.");
    } catch (Exception e) {
      logger.error("Failed to move the passwords database file", e);
      saveFolderPath(previousFolderPath);
      throw e;
    }
  }

  private static void saveFolderPath(String dbFolderPath) {
    DatabaseHandler.dbFolderPath = dbFolderPath;
    App.saveProperty(pwdFileLocationPropertyKey, dbFolderPath);
    logger.info("Database folder path set to: " + dbFolderPath);
  }

  // endregion
}
