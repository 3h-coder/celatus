package com.celatus;

import java.util.List;

import com.celatus.util.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class PasswordsDatabase {
    
    // region =====Variables=====

    private List<Category> categories;

    // endregion

    // region =====Getters and Setters=====

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Category getCategory(String categoryName) {
        if (categories == null) {
            return null;
        }
        for (Category category : categories) {
            if(category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    // endregion

    // region =====Constructors=====

    public PasswordsDatabase() {}

    public PasswordsDatabase(List<Category> categories) {
        this.categories = categories;
    }

    // endregion

    // region =====Instance Methods=====

    public boolean hasCategory(Category category) {
        if (categories != null) {
            return categories.contains(category);
        }
        return false;     
    }

    public boolean hasCategory(String categoryName) {
        if (categories == null) {
            return false;
        }
        for (Category category : categories) {
            if(category.getName().equalsIgnoreCase(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public void addCategory(Category category) {
        if(this.categories != null && this.hasCategory(category.getName())) {
            throw new IllegalArgumentException("There is already a category with the name " + category.getName() + " in the password database");
        }
        if(this.categories == null) {
            this.categories = new ArrayList<Category>();
        }
        this.categories.add(category);
    }

    public void removeCategory(Category category) {
        if (this.categories != null) {
            this.categories.remove(category);
        }
    }

    /**
     * Browses the categories and their nested PasswordEntry objects to calculate ids for categories and password entries without an id.
     */
    public void calculateAllIds() {
        if (this.categories == null) {
            return;
        }

        for (Category category : this.categories) {
            if (category.getId() == null) {
                // Calculate the id
            }

            List<PasswordEntry> passwordEntries = category.getPasswordEntries();
            if (passwordEntries == null) {
                continue;
            }
            for (PasswordEntry passwordEntry : passwordEntries) {
                if (passwordEntry.getId() == null) {
                    // Calculate the id
                }
            }
        }
    }


    /**
     * @return The PasswordDatabase object as a json string
     */
    public String toRawData() {
        return MapUtils.objectToJson(this, true);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
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
        PasswordsDatabase other = (PasswordsDatabase) obj;
        if (categories == null) {
            if (other.categories != null)
                return false;
        } else if (!categories.equals(other.categories))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PasswordsDatabase [categories=" + categories + "]";
    }
    
    // endregion
    
    // region =====Static Methods=====

    /**
     * @return a default PasswordsDatabase object with a few categories and no password entries.
     */
    public static PasswordsDatabase generateDefault() {
        List<Category> categoriesList = Arrays.asList(new Category("General", null),
                                                      new Category("Emails", null),
                                                      new Category("Social media", null),
                                                      new Category("Administrative", null),
                                                      new Category("Shopping", null),
                                                      new Category("Miscellaneous", null));
        return new PasswordsDatabase(categoriesList);
    }

    /**
     * Parses a PasswordsDatabase object from a serialized json string
     * @param rawData : The json string representing the object
     * @return The PasswordsDatabase object
     */
    public static PasswordsDatabase fromRawData(String rawData) {
        return MapUtils.jsonToObject(rawData, PasswordsDatabase.class);
    }

    // endregion

}
