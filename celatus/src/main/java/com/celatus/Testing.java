package com.celatus;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.spec.IvParameterSpec;

import com.celatus.handler.PropertyHandler;
import com.celatus.models.PasswordEntry;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.CryptoUtils;
import com.celatus.util.MapUtils;

/** Class made for all of our non-unit tests */
public class Testing {

  public static void testDataEncryption() {
    String password = "haha";
    String textBlock =
        "This just a simple test to see if my data encryption function actually works.";
    Key key = CryptoUtils.generateAESKey(password);
    IvParameterSpec iv = CryptoUtils.generateIv();
    IvParameterSpec iv2 = CryptoUtils.generateIv();

    String encryptedBlock1 = CryptoUtils.encryptData(textBlock, key, iv);
    String encryptedBlock2 = CryptoUtils.encryptData(textBlock, key, iv2);

    System.out.println("First Block\n" + encryptedBlock1);
    System.out.println("Second Block\n" + encryptedBlock2);
  }

  public static void
      testDataDecryption() { // The decrypted text block should be equal to the plain text original
    // input
    String password = "haha";
    String textBlock =
        "This just a simple test to see if my data deciphering function actually works.";
    Key key = CryptoUtils.generateAESKey(password);
    IvParameterSpec iv = CryptoUtils.generateIv();
    IvParameterSpec iv2 = CryptoUtils.generateIv();

    String encryptedBlock = CryptoUtils.encryptData(textBlock, key, iv);
    String decryptedBlock = CryptoUtils.decryptData(encryptedBlock, key, iv);
    String decryptedBlockOtherIV = CryptoUtils.decryptData(encryptedBlock, key, iv2);

    System.out.println("decryptedBlock : " + decryptedBlock);
    System.out.println("decryptedBlockOtherIV : " + decryptedBlockOtherIV);
  }

  public static void testDataEncryptionIntoFile() {
    String password = "haha";
    String textBlock =
        "This just a simple test to see if my data encryption function actually works.";
    Key key = CryptoUtils.generateAESKey(password);
    byte[] iv = CryptoUtils.generateIV();

    String filePath = "something.clts";

    try {
      CryptoUtils.encryptIntoFile(filePath, textBlock, key, iv);
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  public static void testDataDecrytionFromfile() {
    String password = "haha";
    Key key = CryptoUtils.generateAESKey(password);
    try {
      System.out.println(CryptoUtils.decryptFile("something.clts", key));
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  public static void testDataDecrytionFromfile(String password, String filepath) {
    Key key = CryptoUtils.generateAESKey(password);
    try {
      System.out.println(CryptoUtils.decryptFile(filepath, key));
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  public static void testMapToJson() {
    Map<String, Object> outerMap = new HashMap();
    Map<String, Object> childMap1 = new HashMap();
    Map<String, Object> childMap2 = new HashMap();

    childMap2.put("testy", "testo");
    childMap1.put("childMap2", childMap2);
    outerMap.put("FirstLayer", childMap1);
    outerMap.put("Something", "nothing");

    try {
      String json = MapUtils.mapToJson(outerMap, false);
      System.out.println(json);
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  public static void testObjToJson() {
    PasswordsDatabase pwdDB = PasswordsDatabase.generateDefault();
    PasswordEntry pwdEntry =
        new PasswordEntry(
            "Facebook",
            "https://facebook.com",
            null,
            "fake.email@gmail.com",
            "fake.email@gmail.com",
            "password");
    pwdDB.getCategory("Social Media").addPasswordEntry(pwdEntry);
    System.out.println(MapUtils.objectToJson(pwdDB, true));
  }

  public static void testJsonToObj() {
    String json =
        "{\"categories\":[{\"name\":\"General\",\"passwordEntries\":null},{\"name\":\"Emails\",\"passwordEntries\":null},{\"name\":\"Social"
            + " media\",\"passwordEntries\":[{\"name\":\"Facebook\",\"description\":null,\"identifier\":\"fake.email@gmail.com\",\"password\":\"password\"}]},{\"name\":\"Administrative\",\"passwordEntries\":null},{\"name\":\"Shopping\",\"passwordEntries\":null},{\"name\":\"Miscellaneous\",\"passwordEntries\":null}]}";
    PasswordsDatabase pwdDB = MapUtils.jsonToObject(json, PasswordsDatabase.class);
    System.out.println(pwdDB);
  }

  public static void testIdFromDateTime() {
    LocalDateTime now = LocalDateTime.now();
    System.out.println("Now: " + now);
    long timeStampSeconds = now.atZone(ZoneId.systemDefault()).toEpochSecond();
    System.out.println("Time stamp: " + timeStampSeconds);
    String hash = CryptoUtils.getSHA256Hash(String.valueOf(timeStampSeconds));
    String result = "CAT" + hash.toUpperCase();
    System.out.println("Result: " + result);
  }

  public static void testRemoveFromMap() {
    /*
     * Map <String, Boolean> map = new HashMap<>();
     * map.put("test", true);
     * System.out.println(map);
     * boolean tmp = map.get("test");
     * map.remove("test");
     * System.out.println(tmp);
     * System.out.println(map);
     */
    Map map = null;
    if (map.containsKey("test")) {
      System.out.println("haha");
    }
  }

  public static void testLastIndexOf() {
    String string = "ha, ho, hu, hi, he, ";
    String empty = "";
    System.out.println(string.substring(0, string.lastIndexOf(",")));
    System.out.println(empty.substring(0, empty.lastIndexOf(",")));
  }

  public static void testNestedDict() {
    HashMap<String, Object> map = new HashMap<>();
    HashMap<String, Object> nested1 = new HashMap<>();
    HashMap<String, Object> nested2 = new HashMap<>();

    nested2.put("final", "string");
    nested1.put("nested2", nested2);
    nested1.put("hey", "hoy");
    map.put("nested1", nested1);

    System.out.println("Map: " + map);
    System.out.println(map.get("final"));
  }

  public static void testReadProperties() {
    System.out.println(PropertyHandler.readProperties());
  }

  public static void testWriteProperties() {
    Properties properties = PropertyHandler.readProperties();
    properties.setProperty("testKey", "testValue");
    PropertyHandler.writeProperties(properties);
    System.out.println(PropertyHandler.readProperties());
  }

  public static void main(String[] args) {
    // testDataEncryption();
    // testDataDecryption();
    // testDataEncryptionIntoFile();
    // testDataDecrytionFromfile();
    // testMapToJson();
    // testDataDecrytionFromfile("This is just a test passphrase to test",
    // "passwords.clts");
    testObjToJson();
    // testJsonToObj();
    // testIdFromDateTime();
    // testRemoveFromMap();
    // testWriteSettings();
    // testLoadSettings();
    // testEnumString();
    // testLastIndexOf();
    // testNestedDict();
    // testReadProperties();
    // testWriteProperties();
  }
}
