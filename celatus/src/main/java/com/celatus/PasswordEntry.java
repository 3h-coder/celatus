package com.celatus;

public class PasswordEntry {

    // region =====Variables=====

    private String name;
    private String description;
    private String identifier;
    private String password;

    // endregion

    // region =====Getters and Setters=====

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // endregion

    // region =====Constructor=====

    public PasswordEntry(String name, String description, String identifier, String password) {
        this.name = name;
        this.description = description;
        this.identifier = identifier;
        this.password = password;
    }

    // endregion
    
}
