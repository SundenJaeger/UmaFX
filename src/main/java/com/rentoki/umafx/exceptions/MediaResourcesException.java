package com.rentoki.umafx.exceptions;

public class MediaResourcesException extends RuntimeException {
    public MediaResourcesException() {
        super();
    }

    public MediaResourcesException(String message) {
        super(message);
    }

    public MediaResourcesException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaResourcesException(Throwable cause) {
        super(cause);
    }
}
