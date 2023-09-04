package com.celatus.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.celatus.App;
import com.celatus.Settings;
import com.celatus.UserAction;
import com.celatus.util.MapUtils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to read, write and manage user settings
 */
public class SettingsHandler {

    // region =====Variables=====

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    private static final String SETTINGS_FILE_PATH = "settings.json";

    // endregion

    // region =====Default settings=====

    public static Settings generateDefault() {
        Settings defaultSettings = new Settings();

        Map<UserAction, String> keyBinds = new HashMap<>();
        keyBinds.put(UserAction.CHANGE_MASTER_PWD, "Alt+C");
        keyBinds.put(UserAction.COPY_IDENTIFIER, "Ctrl+I"); 
        keyBinds.put(UserAction.COPY_PASSWORD, "Ctrl+P"); 
        keyBinds.put(UserAction.DELETE_SELECTED, "Ctrl+D");
        keyBinds.put(UserAction.FULL_SCREEN, "F11");
        keyBinds.put(UserAction.NEW_CATEGORY, "Shift+C");
        keyBinds.put(UserAction.NEW_PASSWORD, "Shift+P");
        keyBinds.put(UserAction.OPEN_SETTINGS, "Shift+S");
        keyBinds.put(UserAction.OPEN_WEBSITE, "Ctrl+W");
        keyBinds.put(UserAction.SAVE_DATABASE, "Ctrl+S");

        defaultSettings.put("keybinds", keyBinds);

        return defaultSettings;
    }

    // endregion

    // region =====Static Public methods=====
    
    public static Settings loadSettings() throws IOException {
        if (new File(SETTINGS_FILE_PATH).exists()) {
            String rawSettings = Files.readString(Paths.get(SETTINGS_FILE_PATH), StandardCharsets.UTF_8);
            return new Settings(MapUtils.jsonToMap(rawSettings));
        } else {
            logger.info("Generating default settings");
            saveSettings(generateDefault());
            return loadSettings();
        }
    }

    public static void saveSettings(Settings settings) throws IOException {
        Files.write(Paths.get(SETTINGS_FILE_PATH), MapUtils.objectToJson(settings, true).getBytes(StandardCharsets.UTF_8));
    }

    // endregion
}
