package com.celatus;

import java.security.Key;

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


    public static void main(String[] args) {
        //testDataEncryption();
        //testDataDecryption();
        testDataEncryptionIntoFile();
        testDataDecrytionFromfile();
    }

}
