package com.celatus.models;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.util.MapUtils;

/**
 * Custom value tracker to implement Ctrl+Z and Ctrl+Y on text input controls.
 * Currently used for password and text fields where the default javaFX undo
 * throws an exception.
 */
public class TextInputValueTracker {

    // region =====Variables=====

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    private final int MAX_STATES_NUM = 50;

    private Map<Integer, String> values;

    private int currentValueIndex;

    // endregion

    // region =====Constructor=====

    public TextInputValueTracker() {
        values = new HashMap<Integer, String>();
        currentValueIndex = 0;

        registerNewValue("");
    }

    // endregion

    // region =====Instance Methods=====

    public void registerNewValue(String value) {
        incrementIndex();
        values.put(currentValueIndex, value);
        // logger.debug("Registered the new value: " + value + " at the index " +
        // currentValueIndex);
    }

    public String getPreviousValue() {
        if (currentValueIndex == 0) {
            return null;
        }

        currentValueIndex--;
        return values.get(currentValueIndex);
    }

    public String getNextValue() {
        if (currentValueIndex == values.size()) {
            return null;
        }

        currentValueIndex++;
        return values.get(currentValueIndex);
    }

    // endregion

    // region =====Private Methods=====

    private void incrementIndex() {
        // If we have moved backwards and the user inputs new text, we clear the
        // following values
        if (currentValueIndex < values.size()) {
            MapUtils.removeAllAfter(currentValueIndex, values);
            currentValueIndex++;
            // We make sure we're not going over the limit
        } else if (values.size() == MAX_STATES_NUM) {
            MapUtils.remove(1, values);
        } else {
            currentValueIndex++;
        }
    }

    // endregion
}
