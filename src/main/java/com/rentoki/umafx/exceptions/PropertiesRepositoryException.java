package com.rentoki.umafx.exceptions;

public class PropertiesRepositoryException extends Exception {
    public PropertiesRepositoryException() {
        super();
    }

    public PropertiesRepositoryException(String message) {
        super(message);
    }

    public PropertiesRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesRepositoryException(Throwable cause) {
        super(cause);
    }
}
