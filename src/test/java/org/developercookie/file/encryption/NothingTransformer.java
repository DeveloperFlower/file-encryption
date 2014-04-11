package org.developercookie.file.encryption;

/**
 * One extremely simple implementation of a ContentTransformer. It doesn't do anything and is only for testing. Created
 * by developerCookie on 10.04.14.
 */
public class NothingTransformer implements ContentTransformer {
    /** Returns the <code>content</code> as such.*/
    @Override
    public byte[] encrypt(byte[] content, String key) {
        return content;
    }

    /** Returns the <code>content</code> as such. */
    @Override
    public byte[] decrypt(byte[] content, String key) throws IllegalKeyException {
        return content;
    }
}
