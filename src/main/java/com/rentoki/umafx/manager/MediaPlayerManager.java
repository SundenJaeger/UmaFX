package com.rentoki.umafx.manager;

import com.rentoki.umafx.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Path;
import java.util.List;

public class MediaPlayerManager {
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private Media media;
    private MediaPlayer mediaPlayer;
    private int musicIndex;

    public void play(List<Path> paths) {
        songs.setAll(paths.stream().map(Song::new).toList());

        if (!songs.isEmpty()) {
            musicIndex = 0;
            playSong(musicIndex);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    private void playSong(int index) {
        media = new Media(songs.get(index).path().toUri().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnEndOfMedia(() -> {
            musicIndex = (musicIndex + 1) % songs.size();
            playSong(musicIndex);
        });

        mediaPlayer.play();
    }
}
