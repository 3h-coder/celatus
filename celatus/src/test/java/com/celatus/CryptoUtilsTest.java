package com.celatus;

import java.security.*;

import javax.crypto.spec.IvParameterSpec;

import org.junit.Test;

import com.celatus.util.CryptoUtils;

import org.junit.Assert;

public class CryptoUtilsTest {

  @Test
  public void testKeyGeneration() { // Equal strings should return equal keys
    String password1 = "hahaha";
    String password2 = "hahaha";
    String password3 = "hohoho";

    Assert.assertEquals(
        CryptoUtils.generateAESKey(password1), CryptoUtils.generateAESKey(password2));
    Assert.assertNotEquals(
        CryptoUtils.generateAESKey(password1), CryptoUtils.generateAESKey(password3));
  }

  @Test
  public void testDataEncryption() { // Different IVs should produce different encrypted blocks
    String password = "haha";
    String textBlock =
        "This just a simple test to see if my data encryption function actually works.";
    Key key = CryptoUtils.generateAESKey(password);
    IvParameterSpec iv = CryptoUtils.generateIv();
    IvParameterSpec iv2 = CryptoUtils.generateIv();

    String encryptedBlock1 = CryptoUtils.encryptData(textBlock, key, iv);
    String encryptedBlock2 = CryptoUtils.encryptData(textBlock, key, iv2);

    Assert.assertNotEquals(encryptedBlock1, encryptedBlock2);
  }

  @Test
  public void
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

    Assert.assertEquals(textBlock, decryptedBlock);
    // The same IV should be used for both encryption and decryption, but unlike the key it does not
    // need to be kept secret
    Assert.assertNotEquals(textBlock, decryptedBlockOtherIV);
  }

  @Test
  public void testDataDecryptionFromFile() {
    String password = "haha";
    String textBlock =
        "This just a simple test to see if my data deciphering function actually works.";
    Key key = CryptoUtils.generateAESKey(password);
    byte[] iv = CryptoUtils.generateIV();
    String encryptedFilePath = "encryption_test.clts";

    try {
      CryptoUtils.encryptIntoFile(encryptedFilePath, textBlock, key, iv);
      Assert.assertEquals(textBlock, CryptoUtils.decryptFile(encryptedFilePath, key));
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }
}
