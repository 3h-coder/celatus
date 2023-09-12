package com.celatus;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.PasswordsDatabase;
import com.celatus.Category;
import com.celatus.PasswordEntry;
import com.celatus.UserAction;
import com.celatus.util.MapUtils;

/**
 * Class used to perform Ctrl+Z and Ctrl+Y on the main window
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

    public void addCreation(PasswordEntry pwdEntry, String categoryName) {
        incrementIndex();
        HashMap<String, Object> action = new HashMap<>();
        action.put("action", UserAction.CREATE);
        action.put("pwdEntry", pwdEntry);
        action.put("category", categoryName);
        actions.put(currentActionIndex, action);

    }

    public void addRemoval(PasswordEntry pwdEntry, String categoryName) {
        incrementIndex();
        HashMap<String, Object> action = new HashMap<>();
        action.put("action", UserAction.DELETE);
        action.put("pwdEntry", pwdEntry);
        action.put("category", categoryName);
        actions.put(currentActionIndex, action);
    }

    public void addMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
        incrementIndex();
        HashMap<String, Object> action = new HashMap<>();
        action.put("action", UserAction.MOVE);
        action.put("pwdEntry", pwdEntry);
        action.put("oldCat", oldCat);
        action.put("newCat", newCat);
        actions.put(currentActionIndex, action);
    }

    public void goBackwards() {
        if (currentActionIndex == 0) {
            return;
        }
        // We undo the action
        var action = getCurrentAction();
        PasswordEntry pwdEntry = (PasswordEntry)action.get("pwdEntry");
        UserAction actionName = (UserAction)action.get("action");
        if (actionName == UserAction.CREATE) {
            undoCreation(pwdEntry);
        } else if (actionName == UserAction.DELETE) {
            String categoryName = (String)action.get("category");
            undoDeletion(pwdEntry, categoryName);
        } else if (actionName == UserAction.MOVE) {
            String oldCat = (String)action.get("oldCat");
            String newCat = (String)action.get("newCat");
            undoMovement(pwdEntry, oldCat, newCat);
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
        PasswordEntry pwdEntry = (PasswordEntry)action.get("pwdEntry");
        String categoryName = (String)action.get("category");
        UserAction actionName = (UserAction)action.get("action");
        if (actionName == UserAction.CREATE) {
            redoCreation(pwdEntry, categoryName);
        } else if (actionName == UserAction.DELETE) {
            redoDeletion(pwdEntry, categoryName);
        } else if (actionName == UserAction.MOVE) {
            String oldCat = (String)action.get("oldCat");
            String newCat = (String)action.get("newCat");
            redoMovement(pwdEntry, oldCat, newCat);
        }

    }

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

    private void undoCreation(PasswordEntry pwdEntry) {
        logger.debug("Undoing the creation of the password entry : " + pwdEntry.getName() + " (" + pwdEntry.getId() + ")");
        Category cat = pwdEntry.findCategory();
        cat.removePasswordEntry(pwdEntry);
    }

    private void undoDeletion(PasswordEntry pwdEntry, String categoryName) {
        logger.debug("Undoing the deletion of the password entry : " + pwdEntry.getName() + " (" + pwdEntry.getId() + ")");
        Category category = App.getPasswordsDatabase().getCategory(categoryName);
        category.addPasswordEntry(pwdEntry);
    }

    private void undoMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
        logger.debug("Undoing the displacement of the password entry " + pwdEntry.getName() + " (" + pwdEntry.getId() + ") from " + oldCat + " to " + newCat);
        PasswordsDatabase database = App.getPasswordsDatabase();
        Category oldCategory = database.getCategory(oldCat);
        Category newCategory = database.getCategory(newCat);
        database.movePasswordEntry(pwdEntry, newCategory, oldCategory);
    }

    private void redoCreation(PasswordEntry pwdEntry, String categoryName) {
        logger.debug("Redoing the creation of the password entry : " + pwdEntry.getName() + " (" + pwdEntry.getId() + ")");
        Category category = App.getPasswordsDatabase().getCategory(categoryName);
        category.addPasswordEntry(pwdEntry);
    }

    private void redoDeletion(PasswordEntry pwdEntry, String categoryName) {
        logger.debug("Redoing the deletion of the password entry : " + pwdEntry.getName() + " (" + pwdEntry.getId() + ")");
        Category category = App.getPasswordsDatabase().getCategory(categoryName);
        category.removePasswordEntry(pwdEntry);
    }

    private void redoMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
        logger.debug("Redoing the displacement of the password entry " + pwdEntry.getName() + " (" + pwdEntry.getId() + ") from " + oldCat + " to " + newCat);
         PasswordsDatabase database = App.getPasswordsDatabase();
        Category oldCategory = database.getCategory(oldCat);
        Category newCategory = database.getCategory(newCat);
        database.movePasswordEntry(pwdEntry, oldCategory, newCategory);
    }

    // endregion
    
}
