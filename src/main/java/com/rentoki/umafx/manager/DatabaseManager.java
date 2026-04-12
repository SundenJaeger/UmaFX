package com.rentoki.umafx.manager;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.nio.file.Path;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Path DATABASE_PATH = Path.of(System.getProperty("user.home"), "Documents", "UmaFX", "storage");
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH.resolve("track_history.db");
    private static ConnectionSource connectionSource;

    public static ConnectionSource getConnectionSource() throws SQLException {
        if (connectionSource == null) {
            return connectionSource = new JdbcConnectionSource(DATABASE_URL);
        }
        return connectionSource;
    }

    public static void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
            connectionSource = null;
        }
    }
}
