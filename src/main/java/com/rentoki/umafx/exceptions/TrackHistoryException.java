package com.rentoki.umafx.exceptions;

public class TrackHistoryException extends RuntimeException {
    public TrackHistoryException() {
        super();
    }

    public TrackHistoryException(String message) {
        super(message);
    }

    public TrackHistoryException(String message, Throwable ex) {
        super(message, ex);
    }
}
