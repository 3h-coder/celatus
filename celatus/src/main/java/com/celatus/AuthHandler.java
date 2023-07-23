package com.celatus;

import java.util.Map;
import java.util.HashMap;

public class AuthHandler {
    

    // region =====Static Methods=====

    /**
     * Checks whether two passwords match and if they respect the password constraints
     * @param password1 : The first password
     * @param password2 : The second password
     * @return A unique entry Map with the key saying whether or not the passwords are equal and valid, and the value being a string explaining why the
     * passwords are not valid, if they are not.
     */
    public static Map<Boolean, String> checkPasswords(String password1, String password2) {

        Map<Boolean, String> result = new HashMap();
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
     * @param password : The master password used to create a derived key
     */
    public static void setAppKey(String password) {
        App.setKey(CryptoUtils.generateAESKey(password));
    }

    // endregion

}
