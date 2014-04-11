package org.developercookie.file.encryption;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * We want to test the AESContentTransformer.
 * Created by developerCookie on 08.04.14.
 */
public class AESContentTransformerTest {
    private static final String testString = "jhaslkfhkjshkj";
    private static final byte[] testArray = testString.getBytes();

    /** Nothing to encrypt.*/
    @Test
    public void nothingToEncrypt() throws Exception {
        AESContentTransformer transformer = new AESContentTransformer();
        byte[] encryptedContent = transformer.encrypt(new byte[0], "12");
        byte[] decryptedContent = transformer.decrypt(encryptedContent,"12");

        Assert.assertArrayEquals(new byte[0],decryptedContent);
    }

    /** A simple string will be encrypted.*/
    @Test
    public void simpleTest() throws Exception {
        AESContentTransformer transformer = new AESContentTransformer();
        byte[] encryptedContent = transformer.encrypt(testArray,"12");

        byte[] decryptedContent = transformer.decrypt(encryptedContent,"12");
        Assert.assertArrayEquals(testArray,decryptedContent);
    }

    /** We want to encrypt and decrypt a relative long text with a relative long key.*/
    @Test
    public void longEncryptionTest() throws Exception {
        String textSimulation = RandomStringUtils.random(2048,true,true);
        String longKey  = RandomStringUtils.random(256,true,true);

        AESContentTransformer transformer = new AESContentTransformer();
        byte[] encryptedContent = transformer.encrypt(textSimulation.getBytes(),longKey);
        byte[] decryptedContent = transformer.decrypt(encryptedContent,longKey);

        Assert.assertArrayEquals(textSimulation.getBytes(), decryptedContent);
    }

    /** We take a wrong key.*/
    @Test(expected = IllegalKeyException.class)
    public void wrongDecryptionKey() throws Exception {
        AESContentTransformer transformer = new AESContentTransformer();
        byte[] encryptedContent = transformer.encrypt(testArray,"12");
        byte[] decryptedContent = transformer.decrypt(encryptedContent,"13");

    }
}
