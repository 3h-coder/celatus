package com.celatus;

import java.security.Key;
import java.util.Map;
import java.util.HashMap;

import javax.crypto.spec.IvParameterSpec;

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

        CryptoUtils.encryptIntoFile(filePath, textBlock, key, iv);
    }

    public static void testDataDecrytionFromfile() {
        String password = "haha";
        Key key = CryptoUtils.generateAESKey(password);
        System.out.println(CryptoUtils.decryptFile("something.clts", key));
    }

    public static void testDataDecrytionFromfile(String password, String filepath) {
        Key key = CryptoUtils.generateAESKey(password);
        System.out.println(CryptoUtils.decryptFile(filepath, key));
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


    public static void main(String[] args) {
        //testDataEncryption();
        //testDataDecryption();
        //testDataEncryptionIntoFile();
        //testDataDecrytionFromfile();
        //testMapToJson();
        testDataDecrytionFromfile("This is just a test passphrase to test", "passwords.clts");
    }

}
