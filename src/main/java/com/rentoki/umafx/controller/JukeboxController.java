package com.rentoki.umafx.controller;

import com.rentoki.umafx.MainApplication;
import com.rentoki.umafx.dialog.SongQueueDialog;
import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.manager.AnimationManager;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.model.Song;
import com.rentoki.umafx.service.CharacterPreferenceService;
import com.rentoki.umafx.service.WindowPreferencesService;
import com.rentoki.umafx.util.ContextMenuBuilder;
import com.rentoki.umafx.util.ShowAlert;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JukeboxController {
    private final WindowPreferencesService windowPreferencesService;
    private final CharacterPreferenceService characterPreferenceService;
    private final MediaPlayerManager mediaPlayerManager;
    private final MainApplication mainApplication;

    private final AnimationManager animationManager = new AnimationManager();
    private final StringProperty characterName = new SimpleStringProperty();

    private SongQueueDialog songQueueDialog;

    private double xOffset;
    private double yOffset;
    private double posX;
    private double posY;
    private double lastPosX = -1;
    private double lastPosY = -1;

    @FXML
    private ImageView spriteImageView;
    @FXML
    private ImageView jukeboxImageView;

    public JukeboxController(WindowPreferencesService windowPreferencesService, CharacterPreferenceService characterPreferenceService, MediaPlayerManager mediaPlayerManager, MainApplication mainApplication) {
        this.windowPreferencesService = windowPreferencesService;
        this.characterPreferenceService = characterPreferenceService;
        this.mediaPlayerManager = mediaPlayerManager;
        this.mainApplication = mainApplication;
    }

    @FXML
    private void initialize() {
        characterNameProperty().bind(characterPreferenceService.characterProperty());
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                animationManager.loadAnimation(newValue, spriteImageView);
            }
        });

        mediaPlayerManager.playingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animationManager.playAnimation("ready", spriteImageView, () -> animationManager.playRandomVariant("dance", spriteImageView, null));
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
            posX = event.getScreenX() - xOffset;
            posY = event.getScreenY() - yOffset;

            stage.setX(posX);
            stage.setY(posY);
        }
    }

    @FXML
    private void spriteReleased(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (event.getButton() == MouseButton.PRIMARY) {
            if (posX != lastPosX || posY != lastPosY) {
                System.out.println(posX);
                System.out.println(posY);

                lastPosX = posX;
                lastPosY = posY;

                try {
                    windowPreferencesService.savePos(lastPosX, lastPosY);
                } catch (PreferencesRepositoryException e) {
                    ShowAlert.showError(e.getMessage());
                    stage.setX(0);
                    stage.setY(0);

                    try {
                        windowPreferencesService.savePos(0, 0);
                    } catch (PreferencesRepositoryException ex) {
                        ShowAlert.showError(ex.getMessage());
                    }
                }
            }
        }
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }

    public void openSongQueue() {
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

    private void setupJukeboxContextMenu() {
        final ContextMenu jukeboxContextMenu = new ContextMenuBuilder()
                .addMenuItem("Open Song Queue", this::openSongQueue)
                .addSeparator()
                .addMenuItem(mainApplication.playPauseItem())
                .addMenuItem(mainApplication.stopItem())
                .addMenuItem(mainApplication.skipItem())
                .addSeparator()
                .addMenuItem("Exit", () -> {
                    Platform.setImplicitExit(true);
                    Platform.exit();
                    System.exit(0);
                })
                .build();

        jukeboxImageView.setOnContextMenuRequested(event -> jukeboxContextMenu.show(jukeboxImageView, event.getScreenX(), event.getScreenY()));
    }
}