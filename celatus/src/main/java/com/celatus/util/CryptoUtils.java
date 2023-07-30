package com.celatus.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.LinkOption;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptoUtils {

    // region ======Variables=====

    private static final Logger logger = LogManager.getLogger(CryptoUtils.class.getName());

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // endregion

    // region =====Methods=====

    public static void hideFile(String filePath) throws IOException {
        Files.setAttribute(Paths.get(filePath), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
    }

    public static void hideFile(String filePath, boolean readOnly) throws IOException {
        Files.setAttribute(Paths.get(filePath), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        if(readOnly) {
            Files.setAttribute(Paths.get(filePath), "dos:readonly", true, LinkOption.NOFOLLOW_LINKS);
        }
    }

    public static void unhideFile(String filePath) throws IOException {
        Files.setAttribute(Paths.get(filePath), "dos:hidden", false, LinkOption.NOFOLLOW_LINKS);
        Files.setAttribute(Paths.get(filePath), "dos:readonly", false, LinkOption.NOFOLLOW_LINKS);
    }

    public static void setReadOnly(String filePath, boolean readOnly) throws IOException {
        Files.setAttribute(Paths.get(filePath), "dos:readonly", readOnly, LinkOption.NOFOLLOW_LINKS);
    }

    public static String getSHA256Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates a 256 bits AES key derived from an input pass phrase string 
     * @param masterPwd : The pass phrase used to generate the AES key
     */
    public static Key generateAESKey(String masterPwd) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // Note that salting the pass phrase is not necessary since we're not storing multiple passwords in a database, but to comply with
            // the method we are forced to use a salt.
            KeySpec spec = new PBEKeySpec(masterPwd.toCharArray(), "salt".getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return secret;
        } catch (Exception ex) {
            logger.error("Failed to generate a key: " + ex);
            return null;
        }
    }

    /**
     * Generates a random 16 bytes IV
     * @return The IvParameterSpec object used to cipher each text block
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Generates a random 16 bytes IV
     * @return The 16 bytes array IV used to cipher each text block
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Encrypts the input data
     * @param data : The data string to cipher
     * @param Key : The key used for our AES CBC encryption
     * @param iv : The IV used for our AES CBC encryption
     * @return : The encrypted data as a string
     */
    public static String encryptData(String data, Key Key, IvParameterSpec iv){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, Key, iv);
            byte[] cipherText = cipher.doFinal(data.getBytes());
            String encryptedData = Base64.getEncoder().encodeToString(cipherText);
            return encryptedData;
        } catch (Exception ex) {
            logger.error("Failed to encrypt data: " + ex);
            return null;
        }
    }

    /**
     * Deciphers an encrypted data string
     * @param encryptedData : The data we want to decipher
     * @param key : The key used for our AES deciphering
     * @param iv : The IV used for our AES CBC encryption
     * @return : The deciphered plain text
     */
    public static String decryptData(String encryptedData, Key key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(plainText);
        } catch (Exception ex) {
            logger.error("Failed to decrypt data: " + ex);
            return null;
        }
    }
    
    /**
     * Saves data into a file after ciphering it through AES CBC
     * @param outputFilePath : File where we store the data
     * @param data : Data to be stored into the file
     * @param key : Key used for AES CBC encryption
     * @param iv : The iv byte array used for encryption
     * @return <b>true</b> if the encryption was succesful, <b>false</b> otherwise
     */
    public static void encryptIntoFile(String outputFilePath, String data, Key key, byte[] iv) throws FileNotFoundException,
        IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {
            
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        outputStream.write(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        
        byte[] cipherText = cipher.doFinal(data.getBytes());
        outputStream.write(cipherText);
    }

    /**
     * Decrypts data from an AES CBC encrypted file
     * @param inputFilePath : The file that we want to decrypt
     * @param key : The AES key used for decryption
     * @return The deciphered file content as a String object
     */
    public static String decryptFile(String inputFilePath, Key key) throws InvalidKeyException, 
        InvalidAlgorithmParameterException, FileNotFoundException, NoSuchAlgorithmException,
        NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
            
        InputStream inputStream = new FileInputStream(inputFilePath);
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Read the IV from the input file
        byte[] storedIV = new byte[16];
        inputStream.read(storedIV);
        
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(storedIV));

        byte[] encryptedBytes = inputStream.readAllBytes();
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    // endregion
}
