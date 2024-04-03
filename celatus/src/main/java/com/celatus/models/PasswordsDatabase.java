package com.celatus.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.celatus.util.MapUtils;

/** Object serving as our passwords database, meant to be json serialized */
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

  public PasswordsDatabase() {
  }

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
    for (Category category : this.categories) {
      if (category.getName().equalsIgnoreCase(categoryName)) {
        return true;
      }
    }
    return false;
  }

  public Category getCategory(String categoryName) {
    if (categories == null) {
      return null;
    }
    for (Category category : categories) {
      if (category.getName().equalsIgnoreCase(categoryName)) {
        return category;
      }
    }
    return null;
  }

  public void addCategory(Category category) {
    if (this.categories != null && this.hasCategory(category.getName())) {
      throw new IllegalArgumentException(
          "There is already a category with the name "
              + category.getName()
              + " in the password database");
    }
    if (this.categories == null) {
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
   * Moves a password Entry from one category to another
   *
   * @param pwdEntry
   * @param oldCat
   * @param newCat
   */
  public void movePasswordEntry(PasswordEntry pwdEntry, Category oldCat, Category newCat) {
    if (!this.hasCategory(newCat)) {
      throw new IllegalArgumentException("The category " + newCat.getName() + " does not exist.");
    }
    if (!this.hasCategory(oldCat)) {
      throw new IllegalArgumentException("The category " + oldCat.getName() + " does not exist.");
    }
    if (!oldCat.hasPasswordEntry(pwdEntry)) {
      throw new IllegalArgumentException(
          "The category " + oldCat.getName() + " does not have such password entry.");
    }

    newCat.addPasswordEntry(pwdEntry);
    oldCat.removePasswordEntry(pwdEntry);
  }

  /**
   * Moves a category up in the categories list
   *
   * @param cat
   */
  public void moveCategoryUp(Category cat) {
    int index = this.categories.indexOf(cat);
    if (index == 0) {
      return;
    }
    Category tmp = this.categories.get(index - 1);
    this.categories.set(index - 1, cat);
    this.categories.set(index, tmp);
  }

  /**
   * Moves a category down in the categories list
   *
   * @param cat
   */
  public void moveCategoryDown(Category cat) {
    int index = this.categories.indexOf(cat);
    if (index == this.categories.size() - 1) {
      return;
    }
    Category tmp = this.categories.get(index + 1);
    this.categories.set(index + 1, cat);
    this.categories.set(index, tmp);
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
   * @return a default PasswordsDatabase object with a few categories and no
   *         password entries.
   */
  public static PasswordsDatabase generateDefault() {
    List<Category> categoriesList = Arrays.asList(
        new Category("General", "General passwords", null),
        new Category("Emails", "For example your gmail passwords", null),
        new Category("Social media", "Social media passwords", null),
        new Category("Administrative", "Administrative passwords", null),
        new Category("Shopping", "For example eBay, Amazon or Sephora", null));
    return new PasswordsDatabase(new ArrayList<>(categoriesList));
  }

  /**
   * Parses a PasswordsDatabase object from a serialized json string
   *
   * @param rawData : The json string representing the object
   * @return The PasswordsDatabase object
   */
  public static PasswordsDatabase fromRawData(String rawData) {
    return MapUtils.jsonToObject(rawData, PasswordsDatabase.class);
  }

  // endregion
}
