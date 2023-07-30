package com.celatus;

import java.security.Key;
import java.util.Map;
import java.util.HashMap;

import javax.crypto.spec.IvParameterSpec;

import com.celatus.util.CryptoUtils;
import com.celatus.util.MapUtils;

public class Testing {
    
    public static void testDataEncryption() {
        String password = "haha";
        String textBlock = "This just a simple test to see if my data encryption function actually works.";
        Key key = CryptoUtils.generateAESKey(password);
        IvParameterSpec iv = CryptoUtils.generateIv();
        IvParameterSpec iv2 = CryptoUtils.generateIv();
        
        String encryptedBlock1 = CryptoUtils.encryptData(textBlock, key, iv);
        String encryptedBlock2 = CryptoUtils.encryptData(textBlock, key, iv2);

        System.out.println("First Block\n" + encryptedBlock1);
        System.out.println("Second Block\n" + encryptedBlock2);
    }

    public static void testDataDecryption() { // The decrypted text block should be equal to the plain text original input
        String password = "haha";
        String textBlock = "This just a simple test to see if my data deciphering function actually works.";
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
        String textBlock = "This just a simple test to see if my data encryption function actually works.";
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
        PasswordEntry pwdEntry = new PasswordEntry("Facebook", null, "fake.email@gmail.com", "password");
        pwdDB.getCategory("Social Media").addPasswordEntry(pwdEntry);
        System.out.println(MapUtils.objectToJson(pwdDB, false));
    }

    public static void testJsonToObj() {
        String json = "{\"categories\":[{\"name\":\"General\",\"passwordEntries\":null},{\"name\":\"Emails\",\"passwordEntries\":null},{\"name\":\"Social media\",\"passwordEntries\":[{\"name\":\"Facebook\",\"description\":null,\"identifier\":\"fake.email@gmail.com\",\"password\":\"password\"}]},{\"name\":\"Administrative\",\"passwordEntries\":null},{\"name\":\"Shopping\",\"passwordEntries\":null},{\"name\":\"Miscellaneous\",\"passwordEntries\":null}]}";
        PasswordsDatabase pwdDB = MapUtils.jsonToObject(json, PasswordsDatabase.class);
        System.out.println(pwdDB);
    }


    public static void main(String[] args) {
        //testDataEncryption();
        //testDataDecryption();
        //testDataEncryptionIntoFile();
        //testDataDecrytionFromfile();
        //testMapToJson();
        //testDataDecrytionFromfile("This is just a test passphrase to test", "passwords.clts");
        //testObjToJson();
        testJsonToObj();
    }

}
