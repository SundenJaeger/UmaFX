package com.rentoki.umafx.manager;

import com.rentoki.umafx.enums.PlaybackState;
import com.rentoki.umafx.exceptions.EmptySongListException;
import com.rentoki.umafx.exceptions.MediaPlayerException;
import com.rentoki.umafx.model.Song;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Path;
import java.util.List;

public class MediaPlayerManager {
    private final ObservableList<Song> songs = FXCollections.observableArrayList();

    private MediaPlayer mediaPlayer;
    private int musicIndex;

    private final DoubleProperty volume = new SimpleDoubleProperty(0.5);
    private final SimpleObjectProperty<PlaybackState> state = new SimpleObjectProperty<>(PlaybackState.EMPTY);
    private final BooleanProperty mediaLoaded = new SimpleBooleanProperty(false);

    public MediaPlayerManager() {
        songs.addListener((ListChangeListener<Song>) c -> {
            if (!songs.isEmpty() && state.get() == PlaybackState.EMPTY) {
                state.set(PlaybackState.STOPPED);
            } else if (songs.isEmpty()) {
                disposeCurrentPlayer();
            }
        });
    }

    /* ---------------- Public API ---------------- */

    public void play() {
        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot play: Song list is empty");
        }

        if (state.get() == PlaybackState.PLAYING) {
            return;
        }

        if (mediaPlayer == null) {
            musicIndex = 0;
            playSong(musicIndex);
        } else {
            mediaPlayer.play();
            state.set(PlaybackState.PLAYING);
        }
    }

    public void pause() {
        requireMediaLoaded("pause");

        if (state.get() == PlaybackState.PLAYING) {
            mediaPlayer.pause();
            state.set(PlaybackState.PAUSED);
        }
    }

    public void skip() {
        requireMediaLoaded("skip");

        musicIndex = (musicIndex + 1) % songs.size();
        playSong(musicIndex);
    }

    public void stop() {
        requireMediaLoaded("stop");
        mediaPlayer.stop();
        state.set(PlaybackState.STOPPED);
//        mediaPlayer.dispose();

    }

    public void addSong(List<Path> paths) {
        songs.setAll(paths.stream().map(Song::new).toList());
    }

    /* ---------------- Properties ---------------- */

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public ReadOnlyObjectProperty<PlaybackState> stateProperty() {
        return state;
    }

    public BooleanBinding playProperty() {
        return Bindings.createBooleanBinding(() -> (state.get() == PlaybackState.STOPPED || state.get() == PlaybackState.PAUSED) && !songs.isEmpty(), state, songs);
    }

    public BooleanBinding pauseProperty() {
        return Bindings.createBooleanBinding(() -> state.get() == PlaybackState.PLAYING && mediaLoaded.get() && !songs.isEmpty(), state, songs, mediaLoaded);
    }

    public BooleanBinding stopProperty() {
        return Bindings.createBooleanBinding(() -> (state.get() == PlaybackState.PLAYING || state.get() == PlaybackState.PAUSED) && mediaLoaded.get() && !songs.isEmpty(), state, mediaLoaded, songs);
    }


    public BooleanBinding skipProperty() {
        return Bindings.createBooleanBinding(() -> (state.get() == PlaybackState.PLAYING || state.get() == PlaybackState.PAUSED || state.get() == PlaybackState.STOPPED) && mediaLoaded.get() && songs.size() > 1, mediaLoaded, songs, state);
    }

    /* ---------------- Getters/Setters ---------------- */

    public double getVolume() {
        return volume.get() * 100;
    }

    public void setVolume(double volume) {
        this.volume.set(volume / 100.00);
    }

    public PlaybackState getState() {
        return state.get();
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    /* ---------------- Internals ---------------- */

    private void requireMediaLoaded(String action) {
        if (songs.isEmpty()) {
            throw new EmptySongListException("Cannot " + action + ": Song list is empty");
        }
        if (mediaPlayer == null) {
            throw new MediaPlayerException("Cannot " + action + ": No media loaded");
        }
    }

    private void playSong(int index) {
        disposeCurrentPlayer();

        Song song = songs.get(index);

        try {
            Media media = new Media(song.path().toUri().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.volumeProperty().bind(volume);
            mediaLoaded.set(true);
        } catch (MediaException e) {
            mediaLoaded.set(false);
            throw new MediaPlayerException("Invalid media file: " + song.path(), e);
        }

        mediaPlayer.setOnPaused(() -> state.set(PlaybackState.PAUSED));
        mediaPlayer.setOnPlaying(() -> state.set(PlaybackState.PLAYING));
        mediaPlayer.setOnStopped(() -> state.set(PlaybackState.STOPPED));
        mediaPlayer.setOnEndOfMedia(() -> {
            musicIndex = (musicIndex + 1) % songs.size();
            playSong(musicIndex);
        });
        mediaPlayer.setOnError(() -> {
            mediaLoaded.set(false);
            throw new MediaPlayerException("Media error: " + mediaPlayer.getError().getMessage());
        });

        mediaPlayer.play();
        state.set(PlaybackState.PLAYING);
    }

    private void disposeCurrentPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.volumeProperty().unbind();
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        mediaLoaded.set(false);
        state.set(PlaybackState.EMPTY);
    }
}