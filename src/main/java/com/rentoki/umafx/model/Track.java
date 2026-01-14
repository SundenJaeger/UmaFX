package com.rentoki.umafx.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.file.Path;

public class Track {
    private final ObjectProperty<Path> path = new SimpleObjectProperty<>();
    private final ObjectProperty<TrackMetadata> metadata = new SimpleObjectProperty<>();
    private final StringProperty requester = new SimpleStringProperty();

    public Track(Path path) {
        this.path.set(path);
        this.metadata.set(TrackMetadata.fromFile(path));
        this.requester.set(getSystemUsername());
    }

    /* ---------------- Public API ---------------- */

    @Override
    public String toString() {
        return path.get().getFileName().toString();
    }

    /* ---------------- Properties ---------------- */

    public ObjectProperty<Path> pathProperty() {
        return path;
    }

    public ObjectProperty<TrackMetadata> metadataProperty() {
        return metadata;
    }

    public StringProperty requesterProperty() {
        return requester;
    }

    /* ---------------- Getters/Setters ---------------- */

    public Path getPath() {
        return path.get();
    }

    public TrackMetadata getMetadata() {
        return metadata.get();
    }

    public String getRequester() {
        return requester.get();
    }

    public void setPath(Path path) {
        this.path.set(path);
    }

    public void setMetadata(TrackMetadata metadata) {
        this.metadata.set(metadata);
    }

    public void setRequester(String requester) {
        this.requester.set(requester);
    }

    /* ---------------- Helpers ---------------- */

    private static String getSystemUsername() {
        return System.getProperty("user.name", "Unknown User");
    }
}
