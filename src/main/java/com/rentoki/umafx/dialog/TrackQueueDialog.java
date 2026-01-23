package com.rentoki.umafx.dialog;

import com.rentoki.umafx.controller.TrackQueueController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.util.ButtonUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TrackQueueDialog extends Dialog<ObservableList<Track>> {
    private static final String DIALOG_TITLE = "Track Queue";
    private static final String SAVE_BUTTON_TEXT = "Save";
    private static final String CANCEL_BUTTON_TEXT = "Cancel";
    private static final String SAVE_BUTTON_STYLE_CLASS = "save-button";
    private static final String CANCEL_BUTTON_STYLE_CLASS = "close-button";
    private static final String BUTTON_ORDER = "L_CO";
    private static final double BUTTON_MIN_WIDTH = 136.0;
    private static final double BUTTON_CORNER_RADIUS = 10.0;
    private static final String STYLESHEET_PATH = "/com/rentoki/umafx/css/track-queue.css";

    private TrackQueueController trackQueueController;

    private Button saveButton;
    private Button cancelButton;

    public TrackQueueDialog() {
        setTitle(DIALOG_TITLE);
        setHeaderText(null);

        final DialogPane dialogPane = getDialogPane();
        configureStage(dialogPane);

        dialogPane.setContent(loadTrackQueueView());
        applyStylesheet(dialogPane);
        configureButtons(dialogPane);
        configureButtonBar(dialogPane);
        setResultConverter();

    }

    /* ---------------- Public API ---------------- */

    public void removeAllSong() {
        trackQueueController.removeAllTracks();
    }

    public void setSongs(ObservableList<Track> tracks) {
        trackQueueController.setSongs(tracks);
    }

    /* ---------------- Helpers ---------------- */

    private void configureStage(DialogPane dialogPane) {
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        try {
            stage.getIcons().add(MediaResources.APP_ICON.getImage());
        } catch (MediaResourcesException e) {
            stage.getIcons().clear();
        }
    }

    private Parent loadTrackQueueView() {
        try {
            FXMLLoader loader = new FXMLLoader(TrackQueueDialog.class.getResource(View.TRACK_QUEUE.getFxmlPath()));
            Parent parent = loader.load();
            trackQueueController = loader.getController();

            return parent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureButtons(DialogPane dialogPane) {
        ButtonType saveButtonType = new ButtonType(SAVE_BUTTON_TEXT, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(CANCEL_BUTTON_TEXT, ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogPane.getButtonTypes().addAll(saveButtonType, cancelButtonType);

        saveButton = (Button) dialogPane.lookupButton(saveButtonType);
        saveButton.getStyleClass().add(SAVE_BUTTON_STYLE_CLASS);
        saveButton.disableProperty().bind(Bindings.isEmpty(trackQueueController.getSongs()));

        cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add(CANCEL_BUTTON_STYLE_CLASS);

        ButtonUtils.round(saveButton, BUTTON_CORNER_RADIUS);
        ButtonUtils.round(cancelButton, BUTTON_CORNER_RADIUS);
    }

    private void configureButtonBar(DialogPane dialogPane) {
        var buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setButtonMinWidth(BUTTON_MIN_WIDTH);
            buttonBar.setButtonOrder(BUTTON_ORDER);
        }
    }

    private void setResultConverter() {
        setResultConverter(param -> {
            if (param.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return trackQueueController.getSongs();
            }
            return null;
        });
    }

    private void applyStylesheet(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET_PATH)).toExternalForm());
    }
}