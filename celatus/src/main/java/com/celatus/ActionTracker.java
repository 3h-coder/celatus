package com.celatus;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.util.MapUtils;

/**
 * Class used to perform Ctrl+Z and Ctrl+Y on the main window. Currently supports the creation,
 * deletion and displacement of password entries, as well as the creation and deletion of
 * categories.
 */
public class ActionTracker {

  // region =====Variables=====

  private static final Logger logger = LogManager.getLogger(App.class.getName());

  private final int MAX_ACTIONS_NUM = 20;

  private Map<Integer, Map> actions;

  private int currentActionIndex;

  // endregion

  // region =====Getters and Setters=====

  public Map<Integer, Map> getActions() {
    return actions;
  }

  /*public int getCurrentActionIndex() {
      return currentActionIndex;
  }

  public void setCurrentActionIndex(int currentActionIndex) {
      this.currentActionIndex = currentActionIndex;
  }*/

  // endregion

  // region =====Constructor=====

  public ActionTracker() {
    actions = new HashMap<>();
    currentActionIndex = 0;
  }

  // endregion

  // region =====Instance Methods=====

  public void clear() {
    actions.clear();
    currentActionIndex = 0;
  }

  public void goBackwards() {
    if (currentActionIndex == 0) {
      return;
    }
    // We undo the action
    var action = getCurrentAction();
    UserAction actionName = (UserAction) action.get("action");
    if (action.containsKey("pwdEntry")) {
      PasswordEntry pwdEntry = (PasswordEntry) action.get("pwdEntry");
      if (actionName == UserAction.CREATE) {
        undoPwdCreation(pwdEntry);
      } else if (actionName == UserAction.DELETE) {
        String categoryName = (String) action.get("category");
        undoPwdDeletion(pwdEntry, categoryName);
      } else if (actionName == UserAction.MOVE) {
        String oldCat = (String) action.get("oldCat");
        String newCat = (String) action.get("newCat");
        undoPwdMovement(pwdEntry, oldCat, newCat);
      }
    } else {
      Category category = (Category) action.get("category");
      if (actionName == UserAction.CREATE) {
        undoCatCreation(category);
      } else {
        undoCatDeletion(category);
      }
    }
    // We set the index to the previous action
    currentActionIndex--;
  }

  public void goForwards() {
    if (currentActionIndex == actions.size()) {
      return;
    }
    // We set the index to the next action
    currentActionIndex++;
    // We redo it
    var action = getCurrentAction();
    UserAction actionName = (UserAction) action.get("action");
    if (action.containsKey("pwdEntry")) {
      PasswordEntry pwdEntry = (PasswordEntry) action.get("pwdEntry");
      String categoryName = (String) action.get("category");
      if (actionName == UserAction.CREATE) {
        redoPwdCreation(pwdEntry, categoryName);
      } else if (actionName == UserAction.DELETE) {
        redoPwdDeletion(pwdEntry, categoryName);
      } else if (actionName == UserAction.MOVE) {
        String oldCat = (String) action.get("oldCat");
        String newCat = (String) action.get("newCat");
        redoPwdMovement(pwdEntry, oldCat, newCat);
      }
    } else {
      Category category = (Category) action.get("category");
      if (actionName == UserAction.CREATE) {
        redoCatCreation(category);
      } else {
        redoCatDeletion(category);
      }
    }
  }

  // region -----Category-----

  public void addCatCreation(Category category) {
    incrementIndex();
    HashMap<String, Object> action = new HashMap<>();
    action.put("action", UserAction.CREATE);
    action.put("category", category);
    actions.put(currentActionIndex, action);
  }

  public void addCatRemoval(Category category) {
    incrementIndex();
    HashMap<String, Object> action = new HashMap<>();
    action.put("action", UserAction.DELETE);
    action.put("category", category);
    actions.put(currentActionIndex, action);
  }

  // endregion

  // region -----Password Entry-----

  public void addPwdCreation(PasswordEntry pwdEntry, String categoryName) {
    incrementIndex();
    HashMap<String, Object> action = new HashMap<>();
    action.put("action", UserAction.CREATE);
    action.put("pwdEntry", pwdEntry);
    action.put("category", categoryName);
    actions.put(currentActionIndex, action);
  }

  public void addPwdRemoval(PasswordEntry pwdEntry, String categoryName) {
    incrementIndex();
    HashMap<String, Object> action = new HashMap<>();
    action.put("action", UserAction.DELETE);
    action.put("pwdEntry", pwdEntry);
    action.put("category", categoryName);
    actions.put(currentActionIndex, action);
  }

  public void addPwdMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
    incrementIndex();
    HashMap<String, Object> action = new HashMap<>();
    action.put("action", UserAction.MOVE);
    action.put("pwdEntry", pwdEntry);
    action.put("oldCat", oldCat);
    action.put("newCat", newCat);
    actions.put(currentActionIndex, action);
  }

  // endregion

  @Override
  public String toString() {
    return "ActionTracker [actions=" + actions + ", currentActionIndex=" + currentActionIndex + "]";
  }

  // endregion

  // region =====Private Methods=====

  private void incrementIndex() {
    // If we have moved backwards and the user performs a new action, we clear the following actions
    if (currentActionIndex < actions.size()) {
      MapUtils.removeAllAfter(currentActionIndex, actions);
      currentActionIndex++;
      // We make sure we're not going over the limit
    } else if (actions.size() == MAX_ACTIONS_NUM) {
      MapUtils.remove(1, actions);
    } else {
      currentActionIndex++;
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getCurrentAction() {
    return actions.get(currentActionIndex);
  }

  // region -----Category-----

  private void undoCatCreation(Category category) {
    logger.debug("Undoing the creation of the category : " + category.getName());
    App.getPasswordsDatabase().removeCategory(category);
  }

  private void undoCatDeletion(Category category) {
    logger.debug("Undoing the deletion of the category : " + category.getName());
    App.getPasswordsDatabase().addCategory(category);
  }

  private void redoCatCreation(Category category) {
    logger.debug("Redoing the creation of the category : " + category.getName());
    App.getPasswordsDatabase().addCategory(category);
  }

  private void redoCatDeletion(Category category) {
    logger.debug("Redoing the deletion of the category : " + category.getName());
    App.getPasswordsDatabase().removeCategory(category);
  }

  // endregion

  // region -----Password Entry-----

  private void undoPwdCreation(PasswordEntry pwdEntry) {
    logger.debug(
        "Undoing the creation of the password entry : "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ")");
    Category cat = pwdEntry.findCategory();
    cat.removePasswordEntry(pwdEntry);
  }

  private void undoPwdDeletion(PasswordEntry pwdEntry, String categoryName) {
    logger.debug(
        "Undoing the deletion of the password entry : "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ")");
    Category category = App.getPasswordsDatabase().getCategory(categoryName);
    category.addPasswordEntry(pwdEntry);
  }

  private void undoPwdMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
    logger.debug(
        "Undoing the displacement of the password entry "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ") from "
            + oldCat
            + " to "
            + newCat);
    PasswordsDatabase database = App.getPasswordsDatabase();
    Category oldCategory = database.getCategory(oldCat);
    Category newCategory = database.getCategory(newCat);
    database.movePasswordEntry(pwdEntry, newCategory, oldCategory);
  }

  private void redoPwdCreation(PasswordEntry pwdEntry, String categoryName) {
    logger.debug(
        "Redoing the creation of the password entry : "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ")");
    Category category = App.getPasswordsDatabase().getCategory(categoryName);
    category.addPasswordEntry(pwdEntry);
  }

  private void redoPwdDeletion(PasswordEntry pwdEntry, String categoryName) {
    logger.debug(
        "Redoing the deletion of the password entry : "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ")");
    Category category = App.getPasswordsDatabase().getCategory(categoryName);
    category.removePasswordEntry(pwdEntry);
  }

  private void redoPwdMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
    logger.debug(
        "Redoing the displacement of the password entry "
            + pwdEntry.getName()
            + " ("
            + pwdEntry.getId()
            + ") from "
            + oldCat
            + " to "
            + newCat);
    PasswordsDatabase database = App.getPasswordsDatabase();
    Category oldCategory = database.getCategory(oldCat);
    Category newCategory = database.getCategory(newCat);
    database.movePasswordEntry(pwdEntry, oldCategory, newCategory);
  }

  // endregion

  // endregion

}
