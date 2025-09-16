package com.rentoki.umafx.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ShowAlert {
    private static final String ALERT_INFO_TITLE = "Info";
    private static final String ALERT_ERROR_TITLE = "Error";
    private static final String ALERT_CONFIRMATION_TITLE = "Confirmation";
    private static final String ALERT_WARNING_TITLE = "Warning";

    public static void showInfo(String title, String header, String content) {
        info().text(title, header, content).show();
    }

    public static void showInfo(String header, String content) {
        info().text(ALERT_INFO_TITLE, header, content).show();
    }

    public static void showInfo(String content) {
        info().text(ALERT_INFO_TITLE, null, content).show();
    }

    public static void showError(String title, String header, String content) {
        error().text(title, header, content).show();
    }

    public static void showError(String header, String content) {
        error().text(ALERT_ERROR_TITLE, header, content).show();
    }

    public static void showError(String content) {
        error().text(ALERT_ERROR_TITLE, null, content).show();
    }

    public static Optional<ButtonType> showConfirmation(String title, String header, String content) {
        return confirmation().text(title, header, content).showAndWait();
    }

    public static Optional<ButtonType> showConfirmation(String header, String content) {
        return confirmation().text(ALERT_CONFIRMATION_TITLE, header, content).showAndWait();
    }

    public static Optional<ButtonType> showConfirmation(String content) {
        return confirmation().text(ALERT_CONFIRMATION_TITLE, null, content).showAndWait();
    }

    public static void showWarning(String title, String header, String content) {
        warning().text(title, header, content).show();
    }

    public static void showWarning(String header, String content) {
        warning().text(ALERT_WARNING_TITLE, header, content).show();
    }

    public static void showWarning(String content) {
        warning().text(ALERT_WARNING_TITLE, null, content).show();
    }

    public static AlertBuilder info() {
        return new AlertBuilder(Alert.AlertType.INFORMATION);
    }

    public static AlertBuilder error() {
        return new AlertBuilder(Alert.AlertType.ERROR);
    }

    public static AlertBuilder confirmation() {
        return new AlertBuilder(Alert.AlertType.CONFIRMATION);
    }

    public static AlertBuilder warning() {
        return new AlertBuilder(Alert.AlertType.WARNING);
    }
}