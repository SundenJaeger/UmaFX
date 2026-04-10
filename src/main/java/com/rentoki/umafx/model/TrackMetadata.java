package com.rentoki.umafx.model;

import colorthief.ColorThief;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.manager.CacheManager;
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class TrackMetadata {
    private static final String FALLBACK_ARTIST = "Unknown Artist";
    private static final String FALLBACK_ALBUM = "Unknown Album";
    private static final String FALLBACK_YEAR = "Unknown Year";
    private static final String FALLBACK_TRACK_NUMBER = "Unknown Track Number";
    private static final String FALLBACK_GENRE = "Unknown Genre";
    private static final String FALLBACK_BITRATE = "Unknown Bitrate";
    private static final String FALLBACK_ALBUM_ARTIST = "Unknown Album Artist";
    private static final String FALLBACK_LENGTH = "Unknown Length";
    private static final String FALLBACK_ITEM_TYPE = "Unknown Item Type";

    private final ObjectProperty<Path> path = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty artist = new SimpleStringProperty();
    private final StringProperty album = new SimpleStringProperty();
    private final StringProperty year = new SimpleStringProperty();
    private final String trackNumber;
    private final String genre;
    private final String bitRate;
    private final String albumArtist;
    private final String length;
    private final String itemType;

    private final Tag tag;

    private TrackMetadata(Path path,
                          String title,
                          String artist,
                          String album,
                          String year,
                          String trackNumber,
                          String genre,
                          String bitRate,
                          String albumArtist,
                          String length,
                          String itemType,
                          Tag tag) {
        this.path.set(path);
        this.title.set(title);
        this.artist.set(artist);
        this.album.set(album);
        this.year.set(year);
        this.trackNumber = trackNumber;
        this.genre = genre;
        this.bitRate = bitRate;
        this.albumArtist = albumArtist;
        this.length = length;
        this.itemType = itemType;
        this.tag = tag;
    }

    /* ---------------- Public API ---------------- */

    public static TrackMetadata fromFile(Path path) {
        Path normalizedPath = path.toAbsolutePath().normalize();
        try {
            AudioFile audioFile = AudioFileIO.read(normalizedPath.toFile());
            Tag tag = audioFile.getTag();

            String title = extractField(tag, FieldKey.TITLE, getFileNameWithoutExtension(normalizedPath));
            String artist = extractField(tag, FieldKey.ARTIST, FALLBACK_ARTIST);
            String album = extractField(tag, FieldKey.ALBUM, FALLBACK_ALBUM);
            String year = extractField(tag, FieldKey.YEAR, FALLBACK_YEAR);
            String trackNumber = extractField(tag, FieldKey.TRACK, FALLBACK_TRACK_NUMBER);
            String genre = extractField(tag, FieldKey.GENRE, FALLBACK_GENRE);
            String bitRate = extractField(tag, FieldKey.BPM, FALLBACK_BITRATE);
            String albumArtist = extractField(tag, FieldKey.ALBUM_ARTIST, FALLBACK_ALBUM_ARTIST);
            String length = extractField(tag, FieldKey.TRACK_TOTAL, FALLBACK_LENGTH);
            String itemType = getFileExtension(normalizedPath);

            return new TrackMetadata(normalizedPath,
                    title,
                    artist,
                    album,
                    year,
                    trackNumber,
                    genre,
                    bitRate,
                    albumArtist,
                    length,
                    itemType,
                    tag);
        } catch (IOException | CannotReadException | TagException | ReadOnlyFileException |
                 InvalidAudioFrameException e) {
            return new TrackMetadata(normalizedPath,
                    getFileNameWithoutExtension(normalizedPath),
                    FALLBACK_ARTIST,
                    FALLBACK_ALBUM,
                    FALLBACK_YEAR,
                    FALLBACK_TRACK_NUMBER,
                    FALLBACK_GENRE,
                    FALLBACK_BITRATE,
                    FALLBACK_ALBUM_ARTIST,
                    FALLBACK_LENGTH,
                    FALLBACK_ITEM_TYPE,
                    null);
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

    public String getTrackNumber() {
        return trackNumber;
    }

    public String getGenre() {
        return genre;
    }

    public String getBitRate() {
        return bitRate;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getLength() {
        return length;
    }

    public String getItemType() {
        return itemType;
    }

    public Tag getTag() {
        return tag;
    }

    public Image getAlbumArt() {
        return CacheManager.getOrComputeImage(getPath(), () -> loadAlbumArt(tag));
    }

    public int[] getDominantColor() {
        return CacheManager.getOrComputeDominantColor(getPath(), () -> ColorThief.getColor(SwingFXUtils.fromFXImage(getAlbumArt(), null)));
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

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');

        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return FALLBACK_ITEM_TYPE;
        }

        return fileName.substring(lastDot + 1);
    }

    private static Image loadAlbumArt(Tag tag) {
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
    }
}
