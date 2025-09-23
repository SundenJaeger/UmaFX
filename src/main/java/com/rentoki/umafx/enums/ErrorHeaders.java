package com.rentoki.umafx.enums;

public enum ErrorHeaders {
    GENERAL_ERROR("An error has occurred");

    private final String message;

    ErrorHeaders(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
