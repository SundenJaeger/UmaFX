package com.rentoki.umafx.exceptions;

public class EmptySongListException extends Exception {
    public EmptySongListException() {
        super();
    }

    public EmptySongListException(String message) {
        super(message);
    }

    public EmptySongListException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptySongListException(Throwable cause) {
        super(cause);
    }
}
