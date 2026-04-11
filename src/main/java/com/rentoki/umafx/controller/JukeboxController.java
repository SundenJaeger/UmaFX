package com.rentoki.umafx.controller;

import com.rentoki.umafx.dialog.TrackQueueDialog;
import com.rentoki.umafx.enums.ErrorHeaders;
import com.rentoki.umafx.enums.PlaybackState;
import com.rentoki.umafx.exceptions.InvalidCharacterFolderException;
import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.manager.AnimationManager;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.manager.TrayIconManager;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.service.CharacterPropertiesService;
import com.rentoki.umafx.service.WindowPreferencesService;
import com.rentoki.umafx.util.ContextMenuBuilder;
import com.rentoki.umafx.util.ShowAlert;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JukeboxController {
    private final WindowPreferencesService windowPreferencesService;
    private final CharacterPropertiesService characterPropertiesService;
    private final MediaPlayerManager mediaPlayerManager;
    private final TrayIconManager trayIconManager;

    private final AnimationManager animationManager = new AnimationManager();
    private final StringProperty characterName = new SimpleStringProperty();

    private TrackQueueDialog trackQueueDialog;

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
    @FXML
    private VBox volumeContainer;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;

    public JukeboxController(WindowPreferencesService windowPreferencesService, CharacterPropertiesService characterPropertiesService, MediaPlayerManager mediaPlayerManager, TrayIconManager trayIconManager) {
        this.windowPreferencesService = windowPreferencesService;
        this.characterPropertiesService = characterPropertiesService;
        this.mediaPlayerManager = mediaPlayerManager;
        this.trayIconManager = trayIconManager;
    }

    @FXML
    private void initialize() {
        characterNameProperty().bind(characterPropertiesService.characterProperty());
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                boolean isPlaying = mediaPlayerManager.getState() == PlaybackState.PLAYING;

                animationManager.loadAnimation(newValue, spriteImageView);

                if (isPlaying) {
                    animationManager.playAnimation("ready", spriteImageView, () -> animationManager.playRandomVariant("dance", spriteImageView, null));
                } else {
                    animationManager.playAnimation("idle", spriteImageView, null);
                }
            }
        });

        mediaPlayerManager.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case PLAYING -> animationManager.playAnimation("ready", spriteImageView,
                        () -> animationManager.playRandomVariant("dance", spriteImageView, null));
                case PAUSED, STOPPED, EMPTY -> animationManager.playAnimation("idle", spriteImageView, null);
            }
        });

        setupJukeboxContextMenu();

        volumeSlider.valueProperty().bindBidirectional(
                new SimpleDoubleProperty() {
                    {
                        mediaPlayerManager.volumeProperty().addListener((obs, oldVal, newVal) ->
                                volumeSlider.setValue(newVal.doubleValue() * 100)
                        );
                    }

                    @Override
                    public double get() {
                        return mediaPlayerManager.getVolume();
                    }

                    @Override
                    public void set(double value) {
                        mediaPlayerManager.setVolume(value);
                    }
                }
        );

        volumeLabel.textProperty().bind(volumeSlider.valueProperty().asString("%.0f%%"));
    }

    @FXML
    private void playJukebox(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            openTrackQueue();
        }
    }

    @FXML
    private void spriteClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            changeCharacter();
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

    public VBox getVolumeContainer() {
        return volumeContainer;
    }

    public void openTrackQueue() {
        if (trackQueueDialog == null) {
            trackQueueDialog = new TrackQueueDialog();
        }

        trackQueueDialog.removeAllSong();
        trackQueueDialog.setSongs(mediaPlayerManager.getSongs());

        Optional<ObservableList<Track>> result = trackQueueDialog.showAndWait();

        result.ifPresent(songs -> {
            List<Path> paths = songs.stream().map(Track::getPath).toList();
            mediaPlayerManager.addSong(paths);
        });
    }

    private void setupJukeboxContextMenu() {
        final ContextMenu jukeboxContextMenu = new ContextMenuBuilder()
                .addMenuItem("Open Track Queue", this::openTrackQueue)
                .addSeparator()
                .addMenuItem(trayIconManager.playPauseItem())
                .addMenuItem(trayIconManager.stopItem())
                .addMenuItem(trayIconManager.skipItem())
                .addMenuItem(trayIconManager.volumeItem())
                .addSeparator()
                .addMenuItem("Exit", Platform::exit)
                .build();

        jukeboxImageView.setOnContextMenuRequested(event -> jukeboxContextMenu.show(jukeboxImageView, event.getScreenX(), event.getScreenY()));
        volumeContainer.visibleProperty().bind(trayIconManager.volumeVisibleProperty());
    }


    private void changeCharacter() {
        List<String> availableCharacters;
        try {
            availableCharacters = characterPropertiesService.getAvailableCharacters();
        } catch (InvalidCharacterFolderException e) {
            ShowAlert.showError(ErrorHeaders.GENERAL_ERROR.getMessage(), e.getMessage());
            return;
        }

        if (availableCharacters.isEmpty()) {
            return;
        }

        String currentCharacter = characterName.get();
        int currentIndex = availableCharacters.indexOf(currentCharacter);
        String nextCharacter;

        if (currentIndex == -1 || currentIndex == availableCharacters.size() - 1) {
            nextCharacter = availableCharacters.getFirst();
        } else {
            nextCharacter = availableCharacters.get(currentIndex + 1);
        }

        characterPropertiesService.setCharacter(nextCharacter);
    }
}