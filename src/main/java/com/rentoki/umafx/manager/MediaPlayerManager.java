package com.rentoki.umafx.manager;

import com.rentoki.umafx.exceptions.EmptySongListException;
import com.rentoki.umafx.exceptions.MediaPlayerException;
import com.rentoki.umafx.model.Song;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Path;
import java.util.List;

public class MediaPlayerManager {
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private Media media;
    private MediaPlayer mediaPlayer;
    private int musicIndex;
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final DoubleProperty volume = new SimpleDoubleProperty(0.5);

    public void play() throws EmptySongListException {
        if (!playing.get() && mediaPlayer != null) {
            mediaPlayer.play();
            return;
        }

        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot play: Song list is empty");
        }

        musicIndex = 0;
        playSong(musicIndex);
    }

    public void pause() throws EmptySongListException {
        if (mediaPlayer == null) {
            throw new MediaPlayerException("Cannot pause: No media loaded");
        }

        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot pause: Song list is empty");
        }

        mediaPlayer.pause();
    }

    public void skip() throws EmptySongListException {
        if (mediaPlayer == null) {
            throw new MediaPlayerException("Cannot skip: No media loaded");
        }

        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot skip: Song list is empty");
        }

        musicIndex = (musicIndex + 1) % songs.size();
        playSong(musicIndex);
    }

    public void stop() throws EmptySongListException {
        if (mediaPlayer == null) {
            throw new MediaPlayerException("Cannot stop: No media loaded");
        }

        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot stop: Song list is empty");
        }

        mediaPlayer.stop();
//        mediaPlayer.dispose();

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

    public double getVolume() {
        return volume.get() * 100;
    }

    public void setVolume(double volume) {
        this.volume.set(volume / 100.00);
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    private void playSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.volumeProperty().unbind();
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        Song song = songs.get(index);
        try {
            media = new Media(song.path().toUri().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.volumeProperty().bind(volume);
        } catch (MediaException e) {
            throw new MediaPlayerException("Invalid media file: " + song.path(), e);
        }

        mediaPlayer.setOnPaused(() -> playing.set(false));
        mediaPlayer.setOnPlaying(() -> playing.set(true));
        mediaPlayer.setOnStopped(() -> playing.set(false));
        mediaPlayer.setOnEndOfMedia(() -> {
            musicIndex = (musicIndex + 1) % songs.size();
            playSong(musicIndex);
        });
        mediaPlayer.setOnError(() -> {
            throw new MediaPlayerException("Media error: " + mediaPlayer.getError().getMessage());
        });

        mediaPlayer.play();
    }
}