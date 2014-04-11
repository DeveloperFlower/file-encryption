package org.developercookie.file.encryption;

/**
 * Created by developerCookie on 08.04.14.
 */
public interface ContentTransformer {
    /**
     * Encrypt the given <code>content</code> with the given <code>key</code>.
     */
    byte[] encrypt(byte[] content, String key);

    /**
     * Decrypts the given <code>content</code> with the given <code>key</code>. If this is the incorrect key an
     * IllegalKeyException will be thrown.
     */
    byte[] decrypt(byte[] content, String key) throws IllegalKeyException;
}
