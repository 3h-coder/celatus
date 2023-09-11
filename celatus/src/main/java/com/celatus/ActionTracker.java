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
    }

    public void addCreation(PasswordEntry pwdEntry) {
        incrementIndex();
        HashMap<String, Object> action = new HashMap<>();
        action.put("action", UserAction.CREATE);
        action.put("pwdEntry", pwdEntry);
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
        // We undo the action we're on and set the index to the previous one
        if (currentActionIndex == 1) {
            return;
        }

    }

    public void goForwards() {
        // We set the index to the next action and redo it
        if (currentActionIndex == MAX_ACTIONS_NUM) {
            return;
        }

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
            actions.remove(0);
            MapUtils.remove(0, actions);
        } else {
            currentActionIndex++;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getCurrentAction() {
        return actions.get(currentActionIndex);
    }

    private boolean onCreation() {
        var currentAction = actions.get(currentActionIndex);
        return currentAction.get("action") == UserAction.CREATE;
    }

    private boolean onDeletion() {
        var currentAction = actions.get(currentActionIndex);
        return currentAction.get("action") == UserAction.DELETE;
    }

    private boolean onMove() {
        var currentAction = actions.get(currentActionIndex);
        return currentAction.get("action") == UserAction.MOVE;
    }

    private void undoCreation(PasswordEntry pwdEntry) {
        Category cat = pwdEntry.findCategory();
        cat.removePasswordEntry(pwdEntry);
    }

    private void undoDeletion(PasswordEntry pwdEntry, String categoryName) {
        Category category = App.getPasswordsDatabase().getCategory(categoryName);
        category.addPasswordEntry(pwdEntry);
    }

    private void undoMovement(PasswordEntry pwdEntry, String oldCat, String newCat) {
        PasswordsDatabase database = App.getPasswordsDatabase();
        Category oldCategory = database.getCategory(oldCat);
        Category newCategory = database.getCategory(newCat);
        database.movePasswordEntry(pwdEntry, newCategory, oldCategory);
    }

    // endregion
    
}
