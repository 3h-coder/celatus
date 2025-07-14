package com.celatus.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.celatus.App;
import com.celatus.models.Category;
import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;

/** Static class containing the password search methods */
public class SearchHandler {

  // region =====Variable and Init=====

  private static PasswordsDatabase database = App.getPasswordsDatabase();

  private static final String advancedSearchPrefix = "-";

  // endregion

  // region =====Methods=====

  public static List<PasswordEntry> searchPassword(String searchEntry) {

    if (StringUtils.isBlank(searchEntry)) {
      return null;
    }

    if (searchEntry.startsWith(advancedSearchPrefix)) {
      // advanced search
      return advancedSearch(searchEntry);
    } else {
      // normal search (by name)
      return searchByName(searchEntry);
    }
  }

  private static List<PasswordEntry> advancedSearch(String searchEntry) {
    if ((advancedSearchPrefix + "all").equals(searchEntry)) {
      return getAllPasswordEntries();
    }

    String searchPrefix = StringUtils.EMPTY;
    String searchQuery = StringUtils.EMPTY;
    final String delimiter = ":";

    if (searchEntry.contains(delimiter) && searchEntry.split(delimiter).length > 1) {
      searchPrefix = searchEntry.split(delimiter)[0];
      searchQuery = searchEntry.split(delimiter)[1];
    } else {
      return null;
    }
    switch (searchPrefix) {
      case advancedSearchPrefix + "mail":
      case advancedSearchPrefix + "email":
        return searchByEmail(searchQuery);
      case advancedSearchPrefix + "pwd":
      case advancedSearchPrefix + "password":
        return searchByPwd(searchQuery);
      case advancedSearchPrefix + "id":
      case advancedSearchPrefix + "identifier":
      case advancedSearchPrefix + "username":
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
