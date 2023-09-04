package com.celatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class Settings implements Map<String, Object> {
    
    private Map<String, Object> settingsMap = new HashMap<>();

    // region =====Constructors=====

    public Settings() {}

    public Settings(Map<String, Object> settingsMap) {
        this.settingsMap = settingsMap;
    }

    // endregion

    // region =====Implementing Map interface methods=====

    @Override
    public int size() {
        return settingsMap.size();
    }

    @Override
    public boolean isEmpty() {
        return settingsMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return settingsMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return settingsMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return settingsMap.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return settingsMap.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return settingsMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        settingsMap.putAll(m);
    }

    @Override
    public void clear() {
        settingsMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return settingsMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return settingsMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return settingsMap.entrySet();
    }

    // endregion

    // region =====Custom Methods=====

    @Override
    public String toString() {
        return settingsMap.toString();
    }

    /*@SuppressWarnings("unchecked")
    public void setKeybind(UserAction action, String keyCombination) {
        Map<UserAction, String> keyBinds = (Map<UserAction, String>) settingsMap.get("keybinds");
        keyBinds.put(action, keyCombination);
    }

    @SuppressWarnings({"unchecked", "unlikely-arg-type"})
    public String getKeybind(UserAction action) {
        Map<UserAction, String> keyBinds = (Map<UserAction, String>) settingsMap.get("keybinds");
        return keyBinds.get(action.toString());
    }*/

    // endregion
}
