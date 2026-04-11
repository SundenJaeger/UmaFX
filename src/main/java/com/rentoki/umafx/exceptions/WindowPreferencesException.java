package com.rentoki.umafx.exceptions;

public class WindowPreferencesException extends RuntimeException {
    public WindowPreferencesException() {
        super();
    }

    public WindowPreferencesException(String message) {
        super(message);
    }

    public WindowPreferencesException(String message, Throwable cause) {
        super(message, cause);
    }

    public WindowPreferencesException(Throwable cause) {
        super(cause);
    }
}
