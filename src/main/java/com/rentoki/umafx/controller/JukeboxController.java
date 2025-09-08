package com.rentoki.umafx.controller;

import com.rentoki.umafx.dialog.SongQueueDialog;
import com.rentoki.umafx.manager.AnimationManager;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.model.Song;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JukeboxController {
    private final AnimationManager animationManager = new AnimationManager();
    private final StringProperty characterName = new SimpleStringProperty();
    private final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();

    private SongQueueDialog songQueueDialog;

    @FXML
    private ImageView spriteImageView;

    @FXML
    private void initialize() {
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                animationManager.loadAndStartAnimation(newValue, spriteImageView);
            }
        });
    }

    @FXML
    private void playJukebox() {
        if (songQueueDialog == null) {
            songQueueDialog = new SongQueueDialog();
        }
        Optional<ObservableList<Song>> result = songQueueDialog.showAndWait();

        result.ifPresent(songs -> {
            List<Path> paths = songs.stream().map(Song::path).toList();
            mediaPlayerManager.play(paths);
        });
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }
}