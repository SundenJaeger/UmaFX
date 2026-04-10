package com.rentoki.umafx.dialog;

import com.rentoki.umafx.controller.TrackPropertiesController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.util.ButtonUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class TrackPropertiesDialog extends Dialog<Void> {
    private static final String DIALOG_TITLE = "Track Properties";
    private static final ButtonType CLOSE_BUTTON_TYPE = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    private static final String STYLESHEET_PATH = "/com/rentoki/umafx/css/track-queue.css";
    private static final String CANCEL_BUTTON_STYLE_CLASS = "close-button";
    private static final double BUTTON_MIN_WIDTH = 250.0;
    private static final double BUTTON_CORNER_RADIUS = 10.0;

    private final Track track;

    public TrackPropertiesDialog(Track track) {
        this.track = track;

        setTitle(DIALOG_TITLE);
        setHeaderText(null);

        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET_PATH)).toExternalForm());

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        try {
            stage.getIcons().add(MediaResources.APP_ICON.getImage());
        } catch (MediaResourcesException e) {
            stage.getIcons().clear();
        }

        dialogPane.setContent(loadTrackPropertiesView());
        dialogPane.getButtonTypes().add(CLOSE_BUTTON_TYPE);

        Button cancelButton = (Button) dialogPane.lookupButton(CLOSE_BUTTON_TYPE);
        cancelButton.getStyleClass().add(CANCEL_BUTTON_STYLE_CLASS);
        ButtonUtils.round(cancelButton, BUTTON_CORNER_RADIUS);

        var buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setButtonMinWidth(BUTTON_MIN_WIDTH);
        }
    }

    private Parent loadTrackPropertiesView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentoki/umafx/views/track-properties-view.fxml"));
            loader.setControllerFactory(param -> {
                if (param == TrackPropertiesController.class) {
                    return new TrackPropertiesController(track);
                } else {
                    try {
                        return param.getConstructor().newInstance();
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
