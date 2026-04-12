package com.rentoki.umafx.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.rentoki.umafx.manager.DatabaseManager;
import com.rentoki.umafx.model.TrackHistory;

import java.sql.SQLException;
import java.util.List;

public class TrackHistoryRepository {
    private final Dao<TrackHistory, Integer> dao;

    public TrackHistoryRepository() throws SQLException {
        var connection = DatabaseManager.getConnectionSource();
        TableUtils.createTableIfNotExists(connection, TrackHistory.class);
        dao = DaoManager.createDao(connection, TrackHistory.class);
    }

    public int save(TrackHistory track) throws SQLException {
        return dao.create(track);
    }

    public List<TrackHistory> findAll() throws SQLException {
        return dao.queryForAll();
    }
}
