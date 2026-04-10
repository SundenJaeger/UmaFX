package com.rentoki.umafx.controller;

import com.rentoki.umafx.model.Track;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TrackPropertiesController {
    private final Track track;

    @FXML
    private Label trackTitle;
    @FXML
    private Label trackAlbum;
    @FXML
    private Label trackNumber;
    @FXML
    private Label trackGenre;
    @FXML
    private Label trackBitRate;
    @FXML
    private Label trackContributingArtists;
    @FXML
    private Label trackAlbumArtists;
    @FXML
    private Label trackLength;
    @FXML
    private Label trackYear;
    @FXML
    private Label trackItemType;
    @FXML
    private Label trackFileLocation;

    public TrackPropertiesController(Track track) {
        this.track = track;
    }

    @FXML
    private void initialize() {
        trackTitle.setText(track.getMetadata().getTitle());
        trackAlbum.setText(track.getMetadata().getAlbum());
        trackNumber.setText(track.getMetadata().getTrackNumber());
        trackGenre.setText(track.getMetadata().getGenre());
        trackBitRate.setText(track.getMetadata().getBitRate());
        trackContributingArtists.setText(track.getMetadata().getArtist());
        trackAlbumArtists.setText(track.getMetadata().getAlbumArtist());
        trackLength.setText(track.getMetadata().getLength());
        trackYear.setText(track.getMetadata().getYear());
        trackItemType.setText(track.getMetadata().getItemType());
        trackFileLocation.setText(track.getMetadata().getPath().toString());
    }
}
