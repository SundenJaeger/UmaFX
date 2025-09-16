package com.rentoki.umafx.exceptions;

public class PreferencesRepositoryException extends Exception {
    public PreferencesRepositoryException() {
        super();
    }

    public PreferencesRepositoryException(String message) {
        super(message);
    }

    public PreferencesRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreferencesRepositoryException(Throwable cause) {
        super(cause);
    }
}
