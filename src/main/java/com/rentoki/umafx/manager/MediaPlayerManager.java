package com.rentoki.umafx.manager;

import com.rentoki.umafx.model.Song;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final BooleanProperty playing = new SimpleBooleanProperty(false);

    public void play() {
        if (!playing.get() && mediaPlayer != null) {
            mediaPlayer.play();
            return;
        }

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

    public void skip() {
        if (mediaPlayer != null) {
            musicIndex = (musicIndex + 1) % songs.size();
            playSong(musicIndex);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
//            mediaPlayer.dispose();
        }
    }

    public void addSong(List<Path> paths) {
        songs.setAll(paths.stream().map(Song::new).toList());
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    private void playSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        media = new Media(songs.get(index).path().toUri().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnPaused(() -> playing.set(false));
        mediaPlayer.setOnPlaying(() -> playing.set(true));
        mediaPlayer.setOnStopped(() -> playing.set(false));
        mediaPlayer.setOnEndOfMedia(() -> {
            musicIndex = (musicIndex + 1) % songs.size();
            playSong(musicIndex);
        });

        mediaPlayer.play();
    }
}
