package com.celatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;

import com.celatus.util.CryptoUtils;

public class Category implements Recordable {

    // region =====Variables=====

    private String id;
    private String name;
    private String description;
    private List<PasswordEntry> passwordEntries;
    private LocalDateTime creationDate;

    // endregion

    // region =====Getters and Setters=====

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value.strip();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Category(String name, String description, List<PasswordEntry> passwordEntries) {
        this.creationDate = LocalDateTime.now();
        this.name = name.strip();
        this.description = description;
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((passwordEntries == null) ? 0 : passwordEntries.hashCode());
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (passwordEntries == null) {
            if (other.passwordEntries != null)
                return false;
        } else if (!passwordEntries.equals(other.passwordEntries))
            return false;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + ", description=" + description + ", passwordEntries="
                + passwordEntries + ", creationDate=" + creationDate + "]";
    }

    // endregion

    // region =====Interface Methods=====

    public void calculateID() {
        long creationDateTimeStamp = this.creationDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        String toBeHashed = String.valueOf(creationDateTimeStamp) + this.name;
        this.id = "CAT" + CryptoUtils.getSHA256Hash(toBeHashed).toUpperCase().substring(0, 8);
    }

    // endregion
}
