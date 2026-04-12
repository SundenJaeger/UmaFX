package com.rentoki.umafx.controller;

import com.rentoki.umafx.model.TrackHistory;
import com.rentoki.umafx.util.ImageStorage;
import com.rentoki.umafx.util.TimeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class HistoryRequestCellController {
    @FXML
    private Label trackTitleLabel;
    @FXML
    private Label requesterLabel;
    @FXML
    private ImageView albumImageView;
    @FXML
    private Label timePassedLabel;

    public void setTrackHistory(TrackHistory trackHistory) {
        trackTitleLabel.setText(trackHistory.getTitle());
        requesterLabel.setText(trackHistory.getRequest());
        albumImageView.setImage(ImageStorage.loadImage(trackHistory.getImageHash()));
        timePassedLabel.setText(TimeUtil.toRelativeTime(trackHistory.getRequestDate()));
    }
}
