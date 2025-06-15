package com.celatus;

import org.junit.Assert;
import org.junit.Test;

import com.celatus.constants.Constants;
import com.celatus.models.ActionTracker;
import com.celatus.models.Category;
import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;

public class ActionTrackerTest {

    private final String existingCategoryName = Constants.DefaultCategories.GENERAL_NAME;

    private PasswordEntry pwdEntry = new PasswordEntry("test", "test.com", "test notes", "test@test.com",
            "test@test.com", "pwd");

    @Test
    public void categoryCreation() {
        // Arrange
        var actionTracker = new ActionTracker();

        final String newCategoryName = "Test";
        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        App.setPasswordsDatabase(pwdDB);

        // Act
        Category category = new Category(newCategoryName, "test", null);
        pwdDB.addCategory(category);
        actionTracker.addCatCreation(category);

        // Assert
        Assert.assertNotNull(pwdDB.getCategory(newCategoryName));

        // Act again
        actionTracker.goBackwards();

        // Assert again
        Assert.assertNull(pwdDB.getCategory(newCategoryName));

        // Act again
        actionTracker.goForwards();

        // Assert again
        Assert.assertNotNull(pwdDB.getCategory(newCategoryName));
    }

    @Test
    public void categoryDeletion() {
        // Arrange
        var actionTracker = new ActionTracker();

        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        App.setPasswordsDatabase(pwdDB);
        Category category = pwdDB.getCategory(existingCategoryName);

        // Act
        pwdDB.removeCategory(category);
        actionTracker.addCatRemoval(category);

        // Assert
        Assert.assertNull(pwdDB.getCategory(existingCategoryName));

        // Act again
        actionTracker.goBackwards();

        // Assert again
        Assert.assertNotNull(pwdDB.getCategory(existingCategoryName));

        // Act again
        actionTracker.goForwards();

        // Assert again
        Assert.assertNull(pwdDB.getCategory(existingCategoryName));
    }

    @Test
    public void passwordEntryCreation() {
        // Arrange
        var actionTracker = new ActionTracker();

        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        App.setPasswordsDatabase(pwdDB);
        Category category = pwdDB.getCategory(existingCategoryName);

        // Act
        category.addPasswordEntry(pwdEntry);
        actionTracker.addPwdCreation(pwdEntry, existingCategoryName);

        // Assert
        Assert.assertTrue(category.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goBackwards();

        // Assert again
        Assert.assertFalse(category.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goForwards();

        // Assert again
        Assert.assertTrue(category.hasPasswordEntry(pwdEntry));
    }

    @Test
    public void passwordEntryDeletion() {
        // Arrange
        var actionTracker = new ActionTracker();

        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        Category category = pwdDB.getCategory(existingCategoryName);
        category.addPasswordEntry(pwdEntry);
        App.setPasswordsDatabase(pwdDB);

        // Act
        category.removePasswordEntry(pwdEntry);
        ;
        actionTracker.addPwdRemoval(pwdEntry, existingCategoryName);

        // Assert
        Assert.assertFalse(category.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goBackwards();

        // Assert again
        Assert.assertTrue(category.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goForwards();

        // Assert again
        Assert.assertFalse(category.hasPasswordEntry(pwdEntry));
    }

    @Test
    public void passwordEntryMovement() {
        final String otherCategoryName = Constants.DefaultCategories.EMAILS_NAME;
        // Arrange
        var actionTracker = new ActionTracker();

        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        Category category = pwdDB.getCategory(existingCategoryName);
        Category otherCategory = pwdDB.getCategory(otherCategoryName);
        category.addPasswordEntry(pwdEntry);
        App.setPasswordsDatabase(pwdDB);

        // Act
        category.removePasswordEntry(pwdEntry);
        otherCategory.addPasswordEntry(pwdEntry);
        actionTracker.addPwdMovement(pwdEntry, existingCategoryName, otherCategoryName);

        // Assert
        Assert.assertFalse(category.hasPasswordEntry(pwdEntry));
        Assert.assertTrue(otherCategory.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goBackwards();

        // Assert again
        Assert.assertTrue(category.hasPasswordEntry(pwdEntry));
        Assert.assertFalse(otherCategory.hasPasswordEntry(pwdEntry));

        // Act again
        actionTracker.goForwards();

        // Assert again
        Assert.assertFalse(category.hasPasswordEntry(pwdEntry));
        Assert.assertTrue(otherCategory.hasPasswordEntry(pwdEntry));
    }
}
