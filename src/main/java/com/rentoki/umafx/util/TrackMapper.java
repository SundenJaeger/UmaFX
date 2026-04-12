package com.rentoki.umafx.util;

import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.model.TrackHistory;

public class TrackMapper {
    public static TrackHistory toEntity(Track track) {
        String path = track.getPath().toString();
        String title = track.getMetadata() != null ? track.getMetadata().getTitle() : null;

        return new TrackHistory(
                path,
                title,
                track.getRequester()
        );
    }
}
