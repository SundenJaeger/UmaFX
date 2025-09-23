package com.rentoki.umafx.controller;

import com.rentoki.umafx.enums.ErrorHeaders;
import com.rentoki.umafx.exceptions.DesktopActionException;
import com.rentoki.umafx.model.Song;
import com.rentoki.umafx.util.ContextMenuBuilder;
import com.rentoki.umafx.util.DesktopAction;
import com.rentoki.umafx.util.ShowAlert;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SongQueueController {
    private final ObservableList<Song> songs = FXCollections.observableArrayList();

    @FXML
    private ListView<Song> songQueueListView;
    @FXML
    private Button removeSongButton;
    @FXML
    private Button removeAllSongButton;

    @FXML
    private void initialize() {
        songQueueListView.setItems(songs);
        songQueueListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setupDeleteKeyHandler();
        songQueueListView.setCellFactory(param -> new SongQueueListCell());
        removeSongButton.disableProperty().bind(Bindings.isEmpty(songs).or(songQueueListView.getSelectionModel().selectedItemProperty().isNull()));
        removeAllSongButton.disableProperty().bind(Bindings.isEmpty(songs));
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
                    songs.add(new Song(path));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void addSong(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Music files", "*.mp3;*.wav"));

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            files.forEach(file -> songs.add(new Song(file.toPath())));
        }
    }

    @FXML
    private void removeSong() {
        ObservableList<Song> selectedSongs = songQueueListView.getSelectionModel().getSelectedItems();
        songs.removeAll(selectedSongs);
    }

    public void setSongs(ObservableList<Song> songs) {
        this.songs.setAll(songs);
    }

    public void removeAllSong() {
        songs.clear();
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    private void setupDeleteKeyHandler() {
        songQueueListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeSong();
            }
        });
    }

    private class SongQueueListCell extends ListCell<Song> {
        private final ContextMenu contextMenu = new ContextMenuBuilder()
                .addMenuItem(openFileLocation())
                .addSeparator()
                .addMenuItem("Remove", SongQueueController.this::removeSong)
                .build();

        @Override
        protected void updateItem(Song item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.toString());
                setContextMenu(contextMenu);
            }
        }
    }

    private MenuItem openFileLocation() {
        MenuItem menuItem = new MenuItem("Open file location");

        menuItem.setOnAction(event -> {
            try {
                Song selectedSong = songQueueListView.getSelectionModel().getSelectedItem();
                DesktopAction.openFileLocation(selectedSong.path().toFile());
            } catch (DesktopActionException e) {
                ShowAlert.showError(ErrorHeaders.GENERAL_ERROR.getMessage(), e.getMessage());
            }
        });
        menuItem.disableProperty().bind(Bindings.size(songQueueListView.getSelectionModel().getSelectedItems()).greaterThan(1));

        return menuItem;
    }
}
