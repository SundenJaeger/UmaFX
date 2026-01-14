package com.rentoki.umafx.dialog;

import com.rentoki.umafx.controller.TrackQueueController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.model.Track;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TrackQueueDialog extends Dialog<ObservableList<Track>> {
    private static final String DIALOG_TITLE = "Song Queue";

    private static final ButtonType SAVE_BUTTON_TYPE = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private static final ButtonType CANCEL_BUTTON_TYPE = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    private TrackQueueController trackQueueController;
    private Parent songQueueNode;

    public TrackQueueDialog() {
        setTitle(DIALOG_TITLE);
        setHeaderText(null);

        final DialogPane dialogPane = getDialogPane();

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        try {
            stage.getIcons().add(MediaResources.APP_ICON.getImage());
        } catch (MediaResourcesException e) {
            stage.getIcons().clear();
        }

        dialogPane.setContent(loadSongQueueView());
        dialogPane.getButtonTypes().addAll(SAVE_BUTTON_TYPE, CANCEL_BUTTON_TYPE);
        dialogPane.lookupButton(SAVE_BUTTON_TYPE).disableProperty().bind(Bindings.size(trackQueueController.getSongs()).lessThanOrEqualTo(0));

        setResultConverter(param -> {
            if (param == SAVE_BUTTON_TYPE) {
                return trackQueueController.getSongs();
            }
            return null;
        });
    }

    public void removeAllSong() {
        trackQueueController.removeAllSong();
    }

    public void setSongs(ObservableList<Track> tracks) {
        trackQueueController.setSongs(tracks);
    }

    private Parent loadSongQueueView() {
        if (songQueueNode == null) {
            try {
                FXMLLoader loader = new FXMLLoader(TrackQueueDialog.class.getResource(View.TRACK_QUEUE.getFxmlPath()));
                songQueueNode = loader.load();
                trackQueueController = loader.getController();

                return songQueueNode;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return songQueueNode;
    }
}