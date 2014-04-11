package org.developercookie.file.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Encrypts/Decrypts content with the AES algorithm. The key size is 128bit. Created by developerCookie on 08.04.14.
 */
public class AESContentTransformer implements ContentTransformer {
    /**
     * Encrypts the given <code>content</code> with the given <code>key</code>
     */
    public byte[] encrypt(byte[] content, String key) throws IllegalStateException {
        try {
            return transform(content, key, true);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Makes a decryption of the given <code>content</code> with the specified <code>key</code>. If the key is not
     * correct an IllegalKeyException will be thrown.
     */
    public byte[] decrypt(byte[] content, String key) throws IllegalStateException, IllegalKeyException {
        try {
            return transform(content, key, false);
        } catch (BadPaddingException ex) {
            throw new IllegalKeyException("Key was not correct");
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }


    /**
     * General method for encryption/decryption that is specified by the boolean parameter <code>encrypt</code>
     */
    private byte[] transform(byte[] content, String key, boolean encrypt) throws GeneralSecurityException {
        byte[] hashedKey = hashKey(key);
        byte[] keyToUse = getKey(hashedKey);
        byte[] vParameter = getVVector(hashedKey);
        Key cipherKey = new SecretKeySpec(keyToUse, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec parameterSpec = new IvParameterSpec(vParameter);
        if (encrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, cipherKey, parameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, parameterSpec);
        }
        byte[] encryptedContent = cipher.doFinal(content);

        return encryptedContent;
    }

    /**
     * We need to hash the key to get a 128 bit key for the encryption/decryption and also getting a IV parameter.
     */
    private byte[] hashKey(String toHash) {
        MessageDigest hasher = null;
        try {
            hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("This must not be happend");
        }
        hasher.update(toHash.getBytes());
        byte[] hashedValue = hasher.digest();

        return hashedValue;
    }

    /**
     * Takes the first 16 bytes from the given array.
     */
    private byte[] getKey(byte[] hashedValue) {
        return Arrays.copyOfRange(hashedValue, 0, 16);
    }

    /**
     * Takes the last bytes from the 16th bytes to get a IV parameter.
     */
    private byte[] getVVector(byte[] hashedValue) {
        byte[] lastBytes = Arrays.copyOfRange(hashedValue, 16, hashedValue.length);
        return lastBytes;
    }
}
