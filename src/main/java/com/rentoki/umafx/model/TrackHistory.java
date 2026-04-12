package com.rentoki.umafx.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tracks")
public class TrackHistory {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "path", canBeNull = false)
    private String path;

    @DatabaseField(columnName = "title", canBeNull = false)
    private String title;

    @DatabaseField(columnName = "request", canBeNull = false)
    private String request;

    @DatabaseField(columnName = "request_date", canBeNull = false)
    private long requestDate;

    @DatabaseField(columnName = "image_hash", canBeNull = false)
    private String imageHash;

    public TrackHistory() {
    }

    public TrackHistory(String path, String title, String request) {
        this.path = path;
        this.title = title;
        this.request = request;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(long requestDate) {
        this.requestDate = requestDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    @Override
    public String toString() {
        return title;
    }
}
