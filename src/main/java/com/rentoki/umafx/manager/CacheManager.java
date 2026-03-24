package com.rentoki.umafx.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CacheManager {
    //TODO: Add a logger
    private static final Cache<Path, Image> IMAGE_CACHE = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    //TODO: Add a logger
    private static final Cache<Path, int[]> DOMINANT_COLOR_CACHE = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    public static Image getOrComputeImage(Path key, Supplier<Image> loader) {
        return IMAGE_CACHE.get(key, path -> loader.get());
    }

    public static int[] getOrComputeDominantColor(Path key, Supplier<int[]> loader) {
        return DOMINANT_COLOR_CACHE.get(key, path -> loader.get());
    }
}
