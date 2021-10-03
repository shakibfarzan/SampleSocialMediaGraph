package org.bihe.exception;

/**
 * Throws if in collections exist elements
 */
public class ExistElementException extends Exception{

    public ExistElementException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
