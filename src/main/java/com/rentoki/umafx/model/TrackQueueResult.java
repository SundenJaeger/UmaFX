package com.rentoki.umafx.model;

import javafx.collections.ObservableList;

public record TrackQueueResult(ObservableList<Track> addedTracks, ObservableList<Track> currentTracks) {
}
