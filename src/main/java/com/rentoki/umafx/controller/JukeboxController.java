package com.rentoki.umafx.controller;

import com.rentoki.umafx.dialog.SongQueueDialog;
import com.rentoki.umafx.manager.AnimationManager;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.model.Song;
import com.rentoki.umafx.util.ContextMenuBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
    private ImageView jukeboxImageView;

    @FXML
    private void initialize() {
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                animationManager.loadAnimation(newValue, spriteImageView);
            }
        });

        mediaPlayerManager.playingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animationManager.playAnimation("ready", spriteImageView, () -> animationManager.playAnimation("dance", spriteImageView, null));
            } else {
                animationManager.playAnimation("idle", spriteImageView, null);
            }
        });

        setupJukeboxContextMenu();
    }

    @FXML
    private void playJukebox(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            openSongQueue();
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

    private void setupJukeboxContextMenu() {
        final MenuItem playPauseItem = new MenuItem();

        playPauseItem.textProperty().bind(Bindings.when(mediaPlayerManager.playingProperty()).then("Pause").otherwise("Play"));

        playPauseItem.setOnAction(event -> {
            if (mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.pause();
            } else {
                mediaPlayerManager.play();
            }
        });

        final ContextMenu jukeboxContextMenu = new ContextMenuBuilder()
                .addMenuItem("Open Song Queue", this::openSongQueue)
                .addSeparator()
                .addMenuItem(playPauseItem)
                .addMenuItem("Stop", mediaPlayerManager::stop)
                .addMenuItem("Skip", mediaPlayerManager::skip)
                .addSeparator()
                .addMenuItem("Exit", Platform::exit)
                .build();

        jukeboxImageView.setOnContextMenuRequested(event -> jukeboxContextMenu.show(jukeboxImageView, event.getScreenX(), event.getScreenY()));
    }

    private void openSongQueue() {
        if (songQueueDialog == null) {
            songQueueDialog = new SongQueueDialog();
        }

        songQueueDialog.removeAllSong();
        songQueueDialog.setSongs(mediaPlayerManager.getSongs());

        Optional<ObservableList<Song>> result = songQueueDialog.showAndWait();

        result.ifPresent(songs -> {
            List<Path> paths = songs.stream().map(Song::path).toList();
            mediaPlayerManager.addSong(paths);
        });
    }
}