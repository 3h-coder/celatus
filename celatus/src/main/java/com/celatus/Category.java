package com.celatus;

import java.util.List;

public class Category {

    // region =====Variables=====

    private String name;
    private List<PasswordEntry> passwordEntries;

    // endregion

    // region =====Getters and Setters=====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PasswordEntry> getPasswordEntries() {
        return passwordEntries;
    }

    public void setPasswordEntries(List<PasswordEntry> passwordEntries) {
        this.passwordEntries = passwordEntries;
    }

    // endregion

    // region =====Constructor=====

    public Category(String name, List<PasswordEntry> passwordEntries) {
        this.name = name;
        this.passwordEntries = passwordEntries;
    }

    // endregion
    

    // =====Instance Methods=====

    public void addPasswordEntry(String name, String description, String identifier, String password) {
        PasswordEntry passwordEntry = new PasswordEntry(name, description, identifier, password);
        passwordEntries.add(passwordEntry);
    }

    // endregion

    
}
