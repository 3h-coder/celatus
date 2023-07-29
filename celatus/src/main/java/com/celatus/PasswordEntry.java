package com.celatus;

public class PasswordEntry {

    // region =====Variables=====

    private String id;
    private String name;
    private String description;
    private String identifier;
    private String password;

    // endregion

    // region =====Getters and Setters=====

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
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
        this.name = name.strip();
        this.description = description;
        this.identifier = identifier;
        this.password = password;
    }

    public PasswordEntry(String id, String name, String description, String identifier, String password) {
        this.id = id;
        this.name = name.strip();
        this.description = description;
        this.identifier = identifier;
        this.password = password;
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
    
}
