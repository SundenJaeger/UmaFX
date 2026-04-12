package com.rentoki.umafx.service;

import com.rentoki.umafx.exceptions.TrackHistoryException;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.model.TrackHistory;
import com.rentoki.umafx.repository.TrackHistoryRepository;
import com.rentoki.umafx.util.ImageStorage;
import com.rentoki.umafx.util.TrackMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.Instant;

public class TrackHistoryService {
    private final TrackHistoryRepository trackHistoryRepository;
    private final ObservableList<TrackHistory> histories = FXCollections.observableArrayList();

    public TrackHistoryService() {
        try {
            trackHistoryRepository = new TrackHistoryRepository();
            histories.addAll(trackHistoryRepository.findAll());
        } catch (SQLException e) {
            throw new TrackHistoryException("Failed to instantiate history repository", e);
        }
    }

    public void addTrackHistory(Track track) {
        try {
            TrackHistory entity = TrackMapper.toEntity(track);
            entity.setRequestDate(Instant.now().toEpochMilli());

            String hash = ImageStorage.save(track.getMetadata().getAlbumArt());
            entity.setImageHash(hash);

            trackHistoryRepository.save(entity);
            histories.add(entity);
        } catch (SQLException e) {
            throw new TrackHistoryException("Failed to save track history", e);
        }
    }

    public ObservableList<TrackHistory> getAllTrackHistories() {
        return FXCollections.unmodifiableObservableList(histories);
    }
}
