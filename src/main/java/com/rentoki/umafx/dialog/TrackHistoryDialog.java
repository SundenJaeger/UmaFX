package com.rentoki.umafx.dialog;

import com.rentoki.umafx.controller.TrackHistoryController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.model.TrackHistory;
import com.rentoki.umafx.util.ButtonUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class TrackHistoryDialog extends Dialog<Void> {
    private static final String DIALOG_TITLE = "History";
    private static final ButtonType CANCEL_BUTTON_TYPE = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private static final String STYLESHEET_PATH = "/com/rentoki/umafx/css/track-queue.css";
    private static final String CANCEL_BUTTON_STYLE_CLASS = "close-button";
    private static final double BUTTON_MIN_WIDTH = 250.0;
    private static final double BUTTON_CORNER_RADIUS = 10.0;

    private final ObservableList<TrackHistory> trackEntities;

    public TrackHistoryDialog(ObservableList<TrackHistory> trackEntities) {
        this.trackEntities = trackEntities;

        setTitle(DIALOG_TITLE);
        setHeaderText(null);

        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET_PATH)).toExternalForm());

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        stage.getIcons().add(MediaResources.APP_ICON.getImage());

        dialogPane.setContent(loadTrackHistoryView());
        dialogPane.getButtonTypes().add(CANCEL_BUTTON_TYPE);

        Button cancelButton = (Button) dialogPane.lookupButton(CANCEL_BUTTON_TYPE);
        cancelButton.getStyleClass().add(CANCEL_BUTTON_STYLE_CLASS);
        ButtonUtils.round(cancelButton, BUTTON_CORNER_RADIUS);

        var buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setButtonMinWidth(BUTTON_MIN_WIDTH);
            buttonBar.setButtonOrder("L_C");
        }
    }

    private Parent loadTrackHistoryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentoki/umafx/views/track-history-view.fxml"));
            loader.setControllerFactory(param -> {
                if (param == TrackHistoryController.class) {
                    return new TrackHistoryController(trackEntities);
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
