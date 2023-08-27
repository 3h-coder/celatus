package com.celatus;

import java.util.List;
import java.util.Map;

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
        List<Category> categoriesList = Arrays.asList(new Category("General", null, null),
                                                      new Category("Emails", "For example your gmail passwords.", null),
                                                      new Category("Social media", null, null),
                                                      new Category("Administrative", null, null),
                                                      new Category("Shopping", "For example eBay, Amazon or Sephora.", null),
                                                      new Category("Miscellaneous", null, null));
        return new PasswordsDatabase(new ArrayList<>(categoriesList));
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
