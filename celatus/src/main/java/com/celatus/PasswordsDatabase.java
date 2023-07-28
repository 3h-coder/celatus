package com.celatus;

import java.util.List;
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
        return categories.contains(category);
    }

    public boolean hasCategory(String categoryName) {
        for (Category category : categories) {
            if(category.getName().equalsIgnoreCase(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public void addCategory(Category category) {
        if(!this.hasCategory(category.getName())) {
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
    
    // =====Static Methods=====


    public static PasswordsDatabase generateDefault() {
        List<Category> categoriesList = Arrays.asList(new Category("General", null),
                                                      new Category("Emails", null),
                                                      new Category("Social media", null),
                                                      new Category("Administrative", null),
                                                      new Category("Shopping", null),
                                                      new Category("Miscellaneous", null));
        return new PasswordsDatabase(categoriesList);
    }

    public static PasswordsDatabase fromRawData(String rawData) {
        return MapUtils.jsonToObject(rawData, PasswordsDatabase.class);
    }

    // endregion

}
