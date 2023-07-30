package com.celatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.celatus.util.CryptoUtils;

public class PasswordEntry implements Recordable {

    // region =====Variables=====

    private String ID;
    private String name;
    private String description;
    private String identifier;
    private String password;
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

    public PasswordEntry() {}

    public PasswordEntry(String name, String description, String identifier, String password) {
        this.creationDate = LocalDateTime.now();
        this.name = name.strip();
        this.description = description;
        this.identifier = identifier;
        this.password = password;
        calculateID();
    }

    // endregion

    // region =====Instance Methods=====

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
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
        PasswordEntry other = (PasswordEntry) obj;
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
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PasswordEntry [name=" + name + ", description=" + description + ", identifier=" + identifier
                + ", password=" + password + "]";
    }

    // endregion
    
    // region =====Interface Methods=====

    public void calculateID() {
        long creationDateTimeStamp = this.creationDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        String toBeHashed = String.valueOf(creationDateTimeStamp) + this.name;
        this.ID = "PE" + CryptoUtils.getSHA256Hash(toBeHashed).toUpperCase();
    }

    // endregion
}
