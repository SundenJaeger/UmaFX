package com.rentoki.umafx.enums;

public enum View {
    JUKEBOX("jukebox-view.fxml");

    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = "/com/rentoki/umafx/views/" + fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
