package com.rentoki.umafx.exceptions;

public class DesktopActionException extends RuntimeException {
    public DesktopActionException() {
        super();
    }

    public DesktopActionException(String message) {
        super(message);
    }

    public DesktopActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DesktopActionException(Throwable cause) {
        super(cause);
    }
}
