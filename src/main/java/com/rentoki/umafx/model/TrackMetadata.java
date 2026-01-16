package com.rentoki.umafx.model;

import com.rentoki.colorthief.ColorThief;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.util.Cache;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.coobird.thumbnailator.Thumbnails;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class TrackMetadata {
    private static final String FALLBACK_ARTIST = "Unknown Artist";
    private static final String FALLBACK_ALBUM = "Unknown Album";
    private static final String FALLBACK_YEAR = "Unknown Year";

    private final ObjectProperty<Path> path = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty artist = new SimpleStringProperty();
    private final StringProperty album = new SimpleStringProperty();
    private final StringProperty year = new SimpleStringProperty();
    private final ObjectProperty<Image> albumArt = new SimpleObjectProperty<>();
    private final ObjectProperty<int[]> dominantColor = new SimpleObjectProperty<>();

    private TrackMetadata(Path path, String title, String artist, String album, String year, Image albumArt, int[] dominantColor) {
        this.path.set(path);
        this.title.set(title);
        this.artist.set(artist);
        this.album.set(album);
        this.year.set(year);
        this.albumArt.set(albumArt);
        this.dominantColor.set(dominantColor);
    }

    /* ---------------- Public API ---------------- */

    public static TrackMetadata fromFile(Path path) {
        try {
            AudioFile audioFile = AudioFileIO.read(path.toFile());
            Tag tag = audioFile.getTag();

            String title = extractField(tag, FieldKey.TITLE, getFileNameWithoutExtension(path));
            String artist = extractField(tag, FieldKey.ARTIST, FALLBACK_ARTIST);
            String album = extractField(tag, FieldKey.ALBUM, FALLBACK_ALBUM);
            String year = extractField(tag, FieldKey.YEAR, FALLBACK_YEAR);
            Image albumArt = toFXImage(tag, path);
            int[] dominantColor = extractDominantColor(albumArt, path);

            return new TrackMetadata(path, title, artist, album, year, albumArt, dominantColor);
        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException |
                 InvalidAudioFrameException e) {
            return new TrackMetadata(path, getFileNameWithoutExtension(path), FALLBACK_ARTIST, FALLBACK_ALBUM, FALLBACK_YEAR, MediaResources.FALLBACK_ALBUM_ART.getImage(), new int[]{0, 255, 0});
        }
    }

    /* ---------------- Properties ---------------- */

    public ObjectProperty<Path> pathProperty() {
        return path;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public StringProperty albumProperty() {
        return album;
    }

    public StringProperty yearProperty() {
        return year;
    }

    public ObjectProperty<Image> albumArtProperty() {
        return albumArt;
    }

    public ObjectProperty<int[]> dominantColorProperty() {
        return dominantColor;
    }

    /* ---------------- Getters/Setters ---------------- */

    public Path getPath() {
        return path.get();
    }

    public String getTitle() {
        return title.get();
    }

    public String getArtist() {
        return artist.get();
    }

    public String getAlbum() {
        return album.get();
    }

    public String getYear() {
        return year.get();
    }

    public Image getAlbumArt() {
        return albumArt.get();
    }

    public int[] getDominantColor() {
        return dominantColor.get();
    }

    /* ---------------- Helpers ---------------- */

    private static String extractField(Tag tag, FieldKey key, String fallback) {
        if (tag == null) {
            return fallback;
        }
        String value = tag.getFirst(key);

        return (value != null && !value.isEmpty()) ? value : fallback;
    }

    private static String getFileNameWithoutExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');

        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }

    private static Image toFXImage(Tag tag, Path path) {
        return Cache.getOrCompute(path, () -> {
            if (tag == null) {
                return MediaResources.FALLBACK_ALBUM_ART.getImage();
            }

            Artwork artwork = tag.getFirstArtwork();
            if (artwork == null) {
                return MediaResources.FALLBACK_ALBUM_ART.getImage();
            }

            byte[] imageData = artwork.getBinaryData();
            if (imageData == null || imageData.length == 0) {
                return MediaResources.FALLBACK_ALBUM_ART.getImage();
            }

            try {
                BufferedImage resized = Thumbnails.of(new ByteArrayInputStream(imageData))
                        .size(150, 150)
                        .asBufferedImage();

                return SwingFXUtils.toFXImage(resized, null);
            } catch (IOException e) {
                return MediaResources.FALLBACK_ALBUM_ART.getImage();
            }
        });
    }

    private static int[] extractDominantColor(Image image, Path path) {
        return Cache.getOrCompute(path + "_color", () -> ColorThief.getColor(SwingFXUtils.fromFXImage(image, null)));
    }
}
