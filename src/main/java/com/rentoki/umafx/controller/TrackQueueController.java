package com.rentoki.umafx.controller;

import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.util.ButtonUtils;
import com.rentoki.umafx.util.MenuItemFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TrackQueueController {
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();
    private final ObservableList<Track> addedTracks = FXCollections.observableArrayList();

    private final BooleanProperty hasRemovals = new SimpleBooleanProperty(false);

    private ObservableList<Track> selectedTracks;

    public TrackQueueController(ObservableList<Track> tracks) {
        this.tracks.setAll(tracks);

        addedTracks.addListener((ListChangeListener<Track>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    this.tracks.addAll(c.getAddedSubList());
                }
            }
        });
    }

    /* ---------------- Public API ---------------- */

    public ObservableList<Track> getTracks() {
        return tracks;
    }

    public ObservableList<Track> getAddedTracks() {
        return addedTracks;
    }

    public BooleanProperty hasRemovalsProperty() {
        return hasRemovals;
    }

    /* ---------------- FXML Fields ---------------- */

    @FXML
    private Region metadataBgRegion;
    @FXML
    private Region colorFilterRegion;
    @FXML
    private ImageView albumCover;
    @FXML
    private Label trackTitleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label yearLabel;

    //Action Buttons
    @FXML
    private Button addFolderButton;
    @FXML
    private Button addTrackButton;
    @FXML
    private Button removeTrackButton;
    @FXML
    private Button removeAllTracksButton;

    @FXML
    private ListView<Track> trackListView;

    /* ---------------- FXML Methods ---------------- */

    @FXML
    private void initialize() {
        selectedTracks = trackListView.getSelectionModel().getSelectedItems();
        setActionButtonsShape(addFolderButton, addTrackButton, removeTrackButton, removeAllTracksButton);
        setupDisableButtonProperty();
        setupTrackListView();
        setupDeleteKeyHandler();
    }

    @FXML
    private void addFolder(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File directory = directoryChooser.showDialog(stage);

        if (directory != null) {
            Path directoryPath = directory.toPath();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*{mp3,wav}")) {
                for (Path path : stream) {
                    addedTracks.add(new Track(path));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void addTrack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Music files", "*.mp3;*.wav"));

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            files.forEach(file -> addedTracks.add(new Track(file.toPath())));
        }
    }

    @FXML
    private void removeTrack() {
        if (!selectedTracks.isEmpty()) {
            hasRemovals.set(true);
        }
        addedTracks.removeAll(selectedTracks);
        tracks.removeAll(selectedTracks);
    }

    @FXML
    private void removeAllTracks() {
        if (selectedTracks.isEmpty()) {
            hasRemovals.set(true);
        }
        addedTracks.clear();
        tracks.clear();
    }

    /* ---------------- Helpers ---------------- */

    private void setupDeleteKeyHandler() {
        trackListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeTrack();
            }
        });
    }

    private void setupDisableButtonProperty() {
        removeTrackButton.disableProperty().bind(Bindings.isEmpty(tracks).or(Bindings.isEmpty(selectedTracks)));
        removeAllTracksButton.disableProperty().bind(Bindings.isEmpty(tracks));
    }

    private void setupTrackListView() {
        trackListView.setItems(tracks);
        trackListView.setCellFactory(param -> new TrackListCell());
        trackListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ReadOnlyObjectProperty<Track> selectedSong = trackListView.getSelectionModel().selectedItemProperty();

        metadataBgRegion.backgroundProperty().bind(
                Bindings.createObjectBinding(
                        () -> {
                            if (selectedSong.get() == null) {
                                return createBackground(MediaResources.METADATA_BANNERS.getImage(0));
                            }

                            return createBackground(MediaResources.METADATA_BANNERS.getRandomImage());
                        },
                        selectedSong
                )
        );

        colorFilterRegion.backgroundProperty().bind(
                Bindings.createObjectBinding(
                        () -> {
                            if (selectedSong.get() == null) {
                                return new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
                            }

                            int[] rgb = selectedSong.get().getMetadata().getDominantColor();

                            return new Background(new BackgroundFill(
                                    Color.rgb(rgb[0], rgb[1], rgb[2]),
                                    null, null
                            ));
                        }, selectedSong
                )
        );

        albumCover.imageProperty().bind(
                Bindings.createObjectBinding(
                        () -> selectedSong.get() != null
                                ? selectedSong.get().getMetadata().getAlbumArt()
                                : MediaResources.FALLBACK_ALBUM_ART.getImage(),
                        selectedSong
                )
        );

        trackTitleLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            if (tracks.isEmpty()) {
                                return "No Track Available";
                            }

                            if (selectedSong.get() != null) {
                                return selectedSong.get().getMetadata().getTitle();
                            } else {
                                return "No Track Selected";
                            }
                        }, tracks, selectedSong));

        artistLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> selectedSong.get() != null
                                ? selectedSong.get().getMetadata().getArtist()
                                : "Unknown Artist",
                        selectedSong
                )
        );

        albumLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> selectedSong.get() != null
                                ? selectedSong.get().getMetadata().getAlbum()
                                : "Unknown Album",
                        selectedSong
                )
        );

        yearLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> selectedSong.get() != null
                                ? selectedSong.get().getMetadata().getYear()
                                : "Unknown Year",
                        selectedSong
                )
        );
    }

    private Background createBackground(Image image) {
        return new Background(new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        true, true,
                        false, true
                )
        ));
    }

    private void setActionButtonsShape(Button... buttons) {
        for (Button button : buttons) {
            ButtonUtils.round(button, 10);
        }
    }

    private class TrackListCell extends ListCell<Track> {
        private final ContextMenu contextMenu = MenuItemFactory.createTrackListCellContextMenu(
                trackListView,
                TrackQueueController.this::removeTrack
        );

        private final Parent root;
        private final TrackCellController controller;

        TrackListCell() {
            try {
                FXMLLoader loader = new FXMLLoader(TrackListCell.class.getResource(View.TRACK_CELL.getFxmlPath()));
                root = loader.load();
                controller = loader.getController();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void updateItem(Track item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                controller.setSong(item);
                setGraphic(root);
                setContextMenu(contextMenu);
            }
        }
    }
}
