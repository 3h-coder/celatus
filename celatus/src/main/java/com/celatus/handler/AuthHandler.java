package com.celatus.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.CryptoUtils;

public class AuthHandler {

  // region =====Static Methods=====

  /**
   * Checks whether two passwords match and if they respect the password
   * constraints
   *
   * @param password1 : The first password
   * @param password2 : The second password
   * @return A unique entry Map with the key saying whether or not the passwords
   *         are equal and
   *         valid, and the value being a string explaining why the passwords are
   *         not valid, if they are
   *         not.
   */
  public static Map<Boolean, String> checkPasswords(String password1, String password2) {

    var result = new HashMap<Boolean, String>();
    if (StringUtils.isBlank(password1)) {
      result.put(false, "Please enter your master password");
      return result;
    }
    if (StringUtils.isBlank(password2)) {
      result.put(false, "Please confirm the master password");
      return result;
    }
    if (!password1.equals(password2)) {
      result.put(false, "Both passwords do not match");
    } else {
      // Check if no whitespace at the beginning
      if (password1.startsWith(" ")) {
        result.put(false, "White spaces are not allowed at the start of the master password");
        // Check if no whitespace at the end
      } else if (password1.endsWith(" ")) {
        result.put(false, "White spaces are not allowed at the end of the master password");
      } else {
        // Everything is fine
        result.put(true, null);
      }
    }
    return result;
  }

  /**
   * Checks if the entered master password is correct
   *
   * @param password
   * @return
   */
  public static boolean correctPassword(String password) {
    setAppKey(password);
    DatabaseHandler.parseRawDataFromDatabase();
    return DatabaseHandler.getRawData() != null;
  }

  /**
   * Sets the application's encryption/decryption key from the master password
   *
   * @param password : The master password used to create a derived key
   */
  public static void setAppKey(String password) {
    App.setKey(CryptoUtils.generateAESKey(password));
  }

  /**
   * Performs all the necessary setup after the master password is set
   *
   * @param password
   */
  public static void setAppEntry(String password, boolean masterPwdReset) {
    setAppKey(password);
    if (masterPwdReset) {
      // encrypting the database with the new master password
      DatabaseHandler.saveDatabase();
    } else {
      App.setPasswordsDatabase(PasswordsDatabase.generateDefault());
    }
  }

  /**
   * Performs all the necessary setup after the valid password has been entered
   */
  public static void enterApp() {
    PasswordsDatabase passwordsDatabase = PasswordsDatabase.fromRawData(DatabaseHandler.getRawData());
    App.setPasswordsDatabase(passwordsDatabase);
    App.setOriginalDatabaseHash(passwordsDatabase.hashCode());
  }

  // endregion

}
