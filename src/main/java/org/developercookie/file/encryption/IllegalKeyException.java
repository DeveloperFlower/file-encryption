package org.developercookie.file.encryption;

/**
 * Denotes the use of an incorrect encryption key.
 * Created by developerCookie on 08.04.14.
 */
public class IllegalKeyException extends Exception {
    public IllegalKeyException(String message) {
        super(message);
    }
}
