package com.rentoki.umafx.util;

import java.time.Instant;

public class TimeUtil {
    public static String toRelativeTime(long time) {
        long diff = Instant.now().toEpochMilli() - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d ago";
        }
        if (hours > 0) {
            return hours + "h ago";
        }
        if (minutes > 0) {
            return minutes + "m ago";
        }
        if (seconds > 0) {
            return seconds + "s ago";
        }

        return "Just now";
    }
}
