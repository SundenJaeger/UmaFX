package com.rentoki.umafx.util;

import com.rentoki.umafx.interfaces.IAlertBuilder;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertBuilder implements IAlertBuilder<AlertBuilder> {
    private final Alert alert;

    public AlertBuilder(Alert.AlertType alertType) {
        this.alert = new Alert(alertType);

        final DialogPane dialogPane = this.alert.getDialogPane();
        Stage stage = (Stage) dialogPane.getScene().getWindow();

        stage.setAlwaysOnTop(true);
    }

    @Override
    public AlertBuilder text(String title, String header, String content) {
        this.alert.setTitle(title);
        this.alert.setHeaderText(header);
        this.alert.setContentText(content);

        return this;
    }

    @Override
    public Optional<ButtonType> showAndWait() {
        return this.alert.showAndWait();
    }

    @Override
    public void show() {
        this.alert.show();
    }
}
