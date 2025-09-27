package com.rentoki.umafx.exceptions;

public class InvalidCharacterFolderException extends RuntimeException {
    public InvalidCharacterFolderException() {
        super();
    }

    public InvalidCharacterFolderException(String message) {
        super(message);
    }

    public InvalidCharacterFolderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCharacterFolderException(Throwable cause) {
        super(cause);
    }
}
