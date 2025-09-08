package com.rentoki.umafx.controller;

import com.rentoki.umafx.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
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
    private void initialize() {
        songQueueListView.setItems(songs);
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

    public ObservableList<Song> getSongs() {
        return songs;
    }
}
