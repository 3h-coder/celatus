package com.celatus;

import org.junit.Assert;
import org.junit.Test;

import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.MapUtils;

public class MapUtilsTest {

  @Test
  public void testJsonObjectMapping() {
    PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
    PasswordEntry pwdEntry = new PasswordEntry(
        "Facebook", "https://facebook.com", null, null, "fake.email@gmail.com", "password");
    pwdDB.getCategory("Social Media").addPasswordEntry(pwdEntry);
    String json = MapUtils.objectToJson(pwdDB, true);

    PasswordsDatabase convertedFromJson = MapUtils.jsonToObject(json, PasswordsDatabase.class);

    Assert.assertEquals(pwdDB, convertedFromJson);
  }
}
