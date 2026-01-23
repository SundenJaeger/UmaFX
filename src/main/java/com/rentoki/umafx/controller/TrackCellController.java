package com.rentoki.umafx.controller;

import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.model.TrackMetadata;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class TrackCellController {
    @FXML
    private ImageView albumImageView;
    @FXML
    private Label trackTitleLabel;
    @FXML
    private Label requesterLabel;

    public void setSong(Track track) {
        TrackMetadata metadata = track.getMetadata();

        albumImageView.imageProperty().bind(metadata.albumArtProperty());
        trackTitleLabel.textProperty().bind(metadata.titleProperty());
        requesterLabel.textProperty().bind(track.requesterProperty());
    }
}
