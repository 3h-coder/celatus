package com.celatus.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import com.celatus.App;
import com.celatus.Category;
import com.celatus.PasswordsDatabase;
import com.celatus.PasswordEntry;

import org.apache.commons.lang3.StringUtils;

/** Static class containing the password search methods */
public class SearchHandler {

  // region =====Variable and Init=====

  private static final Logger logger = LogManager.getLogger(SearchHandler.class.getName());

  private static PasswordsDatabase database = App.getPasswordsDatabase();

  // endregion

  // region =====Methods=====

  public static List<PasswordEntry> searchPassword(String searchEntry) {

    if (StringUtils.isBlank(searchEntry)) {
      return null;
    }

    if (searchEntry.startsWith("-")) {
      // advanced search
      return advancedSearch(searchEntry);
    } else {
      // normal search (by name)
      return searchByName(searchEntry);
    }
  }

  private static List<PasswordEntry> advancedSearch(String searchEntry) {
    if ("-all".equals(searchEntry)) {
      return getAllPasswordEntries();
    }

    String searchPrefix = "";
    String searchQuery = "";

    if (searchEntry.contains(":") && searchEntry.split(":").length > 1) {
      searchPrefix = searchEntry.split(":")[0];
      searchQuery = searchEntry.split(":")[1];
    } else {
      return null;
    }
    switch (searchPrefix) {
      case "-mail":
      case "-email":
        return searchByEmail(searchQuery);
      case "-pwd":
      case "-password":
        return searchByPwd(searchQuery);
      case "-id":
      case "-identifier":
      case "-username":
        return searchByIdentifier(searchQuery);
      default:
        return null;
    }
  }

  private static List<PasswordEntry> getAllPasswordEntries() {
    List<PasswordEntry> searchResult = new ArrayList<>();
    for (Category cat : database.getCategories()) {
      if (cat.getPasswordEntries() == null) {
        continue;
      }
      for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
        searchResult.add(pwdEntry);
      }
    }
    return searchResult;
  }

  private static List<PasswordEntry> searchByName(String pwdName) {
    List<PasswordEntry> searchResult = new ArrayList<>();
    pwdName = pwdName.toLowerCase();

    for (Category cat : database.getCategories()) {
      if (cat.getPasswordEntries() == null) {
        continue;
      }
      for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
        String pwdEntryName = pwdEntry.getName().toLowerCase();
        if (pwdEntryName.startsWith(pwdName)) {
          searchResult.add(pwdEntry);
        }
      }
    }

    return searchResult;
  }

  private static List<PasswordEntry> searchByIdentifier(String identifier) {
    List<PasswordEntry> searchResult = new ArrayList<>();

    for (Category cat : database.getCategories()) {
      if (cat.getPasswordEntries() == null) {
        continue;
      }
      for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
        String pwdEntryIdentifier = pwdEntry.getIdentifier().toLowerCase();
        if (pwdEntryIdentifier.startsWith(identifier)) {
          searchResult.add(pwdEntry);
        }
      }
    }

    return searchResult;
  }

  private static List<PasswordEntry> searchByEmail(String email) {
    List<PasswordEntry> searchResult = new ArrayList<>();

    for (Category cat : database.getCategories()) {
      if (cat.getPasswordEntries() == null) {
        continue;
      }
      for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
        String pwdEntryEmail = pwdEntry.getEmail();
        if (StringUtils.isBlank(pwdEntryEmail)) {
          continue;
        }
        if (pwdEntryEmail.startsWith(email)) {
          searchResult.add(pwdEntry);
        }
      }
    }

    return searchResult;
  }

  private static List<PasswordEntry> searchByPwd(String password) {
    List<PasswordEntry> searchResult = new ArrayList<>();

    for (Category cat : database.getCategories()) {
      if (cat.getPasswordEntries() == null) {
        continue;
      }
      for (PasswordEntry pwdEntry : cat.getPasswordEntries()) {
        String pwdEntryPassword = pwdEntry.getPassword().toLowerCase();
        if (pwdEntryPassword.equals(password)) {
          searchResult.add(pwdEntry);
        }
      }
    }

    return searchResult;
  }

  // endregion

}
