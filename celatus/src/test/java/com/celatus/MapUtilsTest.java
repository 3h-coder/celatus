package com.celatus;

import org.junit.Test;
import org.junit.Assert;

public class MapUtilsTest {
    
    @Test
    public void testJsonObjectMapping() {
        PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
        PasswordEntry pwdEntry = new PasswordEntry("Facebook", null, "fake.email@gmail.com", "password");
        pwdDB.getCategory("Social Media").addPasswordEntry(pwdEntry);
        String json = MapUtils.objectToJson(pwdDB, true);

        PasswordsDatabase convertedFromJson = MapUtils.jsonToObject(json, PasswordsDatabase.class);

        Assert.assertEquals(pwdDB, convertedFromJson);
    }
}
