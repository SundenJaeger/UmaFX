package com.rentoki.umafx.dialog;

import com.rentoki.umafx.controller.SongQueueController;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.model.Song;
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

public class SongQueueDialog extends Dialog<ObservableList<Song>> {
    private static final String DIALOG_TITLE = "Song Queue";

    private static final ButtonType SAVE_BUTTON_TYPE = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private static final ButtonType CANCEL_BUTTON_TYPE = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    private SongQueueController songQueueController;
    private Parent songQueueNode;

    public SongQueueDialog() {
        setTitle(DIALOG_TITLE);
        setHeaderText(null);

        final DialogPane dialogPane = getDialogPane();

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        dialogPane.setContent(loadSongQueueView());
        dialogPane.getButtonTypes().addAll(SAVE_BUTTON_TYPE, CANCEL_BUTTON_TYPE);
        dialogPane.lookupButton(SAVE_BUTTON_TYPE).disableProperty().bind(Bindings.size(songQueueController.getSongs()).lessThanOrEqualTo(0));

        setResultConverter(param -> {
            if (param == SAVE_BUTTON_TYPE) {
                return songQueueController.getSongs();
            }
            return null;
        });
    }

    private Parent loadSongQueueView() {
        if (songQueueNode == null) {
            try {
                FXMLLoader loader = new FXMLLoader(SongQueueDialog.class.getResource(View.SONG_QUEUE.getFxmlPath()));
                songQueueNode = loader.load();
                songQueueController = loader.getController();

                return songQueueNode;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return songQueueNode;
    }
}