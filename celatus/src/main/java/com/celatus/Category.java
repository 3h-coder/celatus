package com.celatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;

import com.celatus.util.CryptoUtils;

public class Category implements Recordable {

    // region =====Variables=====

    private String ID;
    private String name;
    private List<PasswordEntry> passwordEntries;
    private LocalDateTime creationDate;

    // endregion

    // region =====Getters and Setters=====

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value.strip();
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
        this.creationDate = LocalDateTime.now();
        this.name = name.strip();
        this.passwordEntries = passwordEntries;
        calculateID();
    }

    // endregion
    
    // region =====Instance Methods=====

    public boolean hasPasswordEntry(PasswordEntry passwordEntry) {
        if (this.passwordEntries == null) {
            return false;
        }
        for (PasswordEntry pwdEntry : this.passwordEntries) {
            if (pwdEntry.equals(passwordEntry)) {
                return true;
            }
        }
        return false;
    }

    public void addPasswordEntry(String name, String description, String identifier, String password) {
        if (this.passwordEntries == null) {
            this.passwordEntries = new ArrayList<PasswordEntry>();
        }

        PasswordEntry passwordEntry = new PasswordEntry(name, description, identifier, password);
        if (this.hasPasswordEntry(passwordEntry)) {
            throw new IllegalArgumentException("This password entry already exists in the password entries list! ");
        }
        this.passwordEntries.add(passwordEntry);
    }

    public void addPasswordEntry(PasswordEntry passwordEntry) {
        if (this.passwordEntries == null) {
            this.passwordEntries = new ArrayList<PasswordEntry>();
        }

        if (this.hasPasswordEntry(passwordEntry)) {
            throw new IllegalArgumentException("This password entry already exists in the password entries list! ");
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

    // region =====Interface Methods=====

    public void calculateID() {
        long creationDateTimeStamp = this.creationDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        String toBeHashed = String.valueOf(creationDateTimeStamp) + this.name;
        this.ID = "CAT" + CryptoUtils.getSHA256Hash(toBeHashed).toUpperCase();
    }

    // endregion
}
