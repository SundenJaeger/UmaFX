package com.rentoki.umafx.controller;

import com.rentoki.umafx.dialog.SongQueueDialog;
import com.rentoki.umafx.manager.AnimationManager;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.model.Song;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JukeboxController {
    private final AnimationManager animationManager = new AnimationManager();
    private final StringProperty characterName = new SimpleStringProperty();
    private final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();

    private SongQueueDialog songQueueDialog;

    private double xOffset;
    private double yOffset;

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
    private void playJukebox(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (songQueueDialog == null) {
                songQueueDialog = new SongQueueDialog();
            }

            Optional<ObservableList<Song>> result = songQueueDialog.showAndWait();

            result.ifPresent(songs -> {
                List<Path> paths = songs.stream().map(Song::path).toList();
                mediaPlayerManager.addSong(paths);
            });
        }
    }

    @FXML
    private void spritePressed(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (event.getButton() == MouseButton.PRIMARY) {
            xOffset = event.getScreenX() - stage.getX();
            yOffset = event.getScreenY() - stage.getY();
        }
    }

    @FXML
    private void spriteDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (event.getButton() == MouseButton.PRIMARY) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }
}