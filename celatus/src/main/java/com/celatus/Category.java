package com.celatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.util.CryptoUtils;
import com.celatus.util.CustomDateUtils;

/**
 * Object to categorize and group passwords
 */
public class Category implements Recordable {

    // region =====Variables=====

    private String id;
    private String name;
    private String description;
    private List<PasswordEntry> passwordEntries;
    private LocalDateTime creationDate;
    private LocalDateTime lastEditDate;
    private List<Map<String, String>> records;

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

    public LocalDateTime getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(LocalDateTime lastEditDate) {
        this.lastEditDate = lastEditDate;
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
        if (StringUtils.isNotBlank(description)) {this.description = description;}
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

    public void addPasswordEntry(PasswordEntry passwordEntry) {
        if (this.passwordEntries == null) {
            this.passwordEntries = new ArrayList<PasswordEntry>();
        }

        if (this.hasPasswordEntry(passwordEntry)) {
            throw new IllegalArgumentException("This password entry already exists in the password entries list! ");
        }
        this.passwordEntries.add(passwordEntry);
    }

    public void removePasswordEntry(PasswordEntry passwordEntry) {
        if (!this.hasPasswordEntry(passwordEntry)) {
            throw new IllegalArgumentException("This password entry does not exist in the password entries list! ");
        }
        this.passwordEntries.remove(passwordEntry);
        if (this.passwordEntries.isEmpty()) {
            this.passwordEntries = null;
        }
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
        result = prime * result + ((lastEditDate == null) ? 0 : lastEditDate.hashCode());
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
        if (lastEditDate == null) {
            if (other.lastEditDate != null)
                return false;
        } else if (!lastEditDate.equals(other.lastEditDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + ", description=" + description + ", passwordEntries="
                + passwordEntries + ", creationDate=" + creationDate + ", lastEditDate=" + lastEditDate + "]";
    }

    // endregion

    // region =====Interface Methods=====

    public void calculateID() {
        long creationDateTimeStamp = this.creationDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        String toBeHashed = String.valueOf(creationDateTimeStamp) + this.name;
        this.id = "CAT" + CryptoUtils.getSHA256Hash(toBeHashed).toUpperCase().substring(0, 8);
    }

    public void saveRecord(String changes) {
        Map<String, String> record = new HashMap<>();
        record.put("Date", CustomDateUtils.prettyDate(LocalDateTime.now()));
        record.put("name", this.name);
        record.put("description", this.description);

        if (this.records == null) {
            this.records = new ArrayList<Map<String, String>>();
            this.records.add(record);
        }
    }

    // endregion
}
