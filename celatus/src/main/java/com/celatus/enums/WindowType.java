package com.celatus.enums;

public enum WindowType {
    MAIN("mainWindow"),
    ENTRY("entryWindow"),
    SETUP("setupWindow"),
    POPUP("popupWindow"),
    CATEGORY("categoryWindow"),
    PASSWORD("passwordWindow"),
    VIEW_PASSWORD("viewPasswordWindow");

    private final String name;

    WindowType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
