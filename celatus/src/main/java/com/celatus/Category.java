package com.celatus;

import java.util.List;
import java.util.ArrayList;

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
        this.name = name.strip();
    }

    public List<PasswordEntry> getPasswordEntries() {
        return passwordEntries;
    }

    public void setPasswordEntries(List<PasswordEntry> passwordEntries) {
        this.passwordEntries = passwordEntries;
    }

    // endregion

    // region =====Constructor=====

    public Category() {}

    public Category(String name, List<PasswordEntry> passwordEntries) {
        this.name = name.strip();
        this.passwordEntries = passwordEntries;
    }

    // endregion
    

    // =====Instance Methods=====

    public void addPasswordEntry(String name, String description, String identifier, String password) {
        if (this.passwordEntries == null) {
            this.passwordEntries = new ArrayList<PasswordEntry>();
        }

        PasswordEntry passwordEntry = new PasswordEntry(name, description, identifier, password);
        this.passwordEntries.add(passwordEntry);
    }

    public void addPasswordEntry(PasswordEntry passwordEntry) {
        if (this.passwordEntries == null) {
            this.passwordEntries = new ArrayList<PasswordEntry>();
        }

        this.passwordEntries.add(passwordEntry);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((passwordEntries == null) ? 0 : passwordEntries.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Category other = (Category) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (passwordEntries == null) {
            if (other.passwordEntries != null)
                return false;
        } else if (!passwordEntries.equals(other.passwordEntries))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [name=" + name + ", passwordEntries=" + passwordEntries + "]";
    }

    // endregion

    
}
