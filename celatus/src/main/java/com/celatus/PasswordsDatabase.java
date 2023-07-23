package com.celatus;

import java.util.List;
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

    // endregion

    // region =====Constructors=====

    public PasswordsDatabase(List<Category> categories) {
        this.categories = categories;
    }

    // endregion

    // region =====Instance Methods=====

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
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

    // endregion

}
