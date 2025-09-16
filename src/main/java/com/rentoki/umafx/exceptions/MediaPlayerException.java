package com.rentoki.umafx.exceptions;

public class MediaPlayerException extends RuntimeException {
    public MediaPlayerException() {
        super();
    }

    public MediaPlayerException(String message) {
        super(message);
    }

    public MediaPlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaPlayerException(Throwable cause) {
        super(cause);
    }
}
