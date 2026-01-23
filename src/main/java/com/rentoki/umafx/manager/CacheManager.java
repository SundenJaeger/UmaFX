package com.rentoki.umafx.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CacheManager {
    private static final Cache<Path, Image> IMAGE_CACHE = Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    private static final Cache<Path, int[]> DOMINANT_COLOR_CACHE = Caffeine.newBuilder()
            .maximumSize(500)
            .recordStats()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    /**
     * Retrieves an {@link Image} from the cache associated with the given {@link Path}.
     * If the image is not already cached, it is loaded using the provided {@link Supplier}
     * and then stored in the cache for future use.
     *
     * <p>Cache statistics are logged each time this method is called.</p>
     *
     * @param key the file path used as the cache key
     * @param loader a {@link Supplier} that provides the image if it is not already cached
     * @return the cached or newly loaded {@link Image}
     */
    public static Image getOrComputeImage(Path key, Supplier<Image> loader) {
        logImageCacheStats();
        return IMAGE_CACHE.get(key, path -> loader.get());
    }

    /**
     * Retrieves the dominant color of an image from the cache associated with the given {@link Path}.
     * If the dominant color is not already cached, it is computed using the provided {@link Supplier}
     * and then stored in the cache for future use.
     *
     * <p>Cache statistics are logged each time this method is called.</p>
     *
     * @param key the file path used as the cache key
     * @param loader a {@link Supplier} that computes the dominant color if it is not already cached
     * @return an array of three integers representing the RGB values of the dominant color
     */
    public static int[] getOrComputeDominantColor(Path key, Supplier<int[]> loader) {
        logDominantColorCache();
        return DOMINANT_COLOR_CACHE.get(key, path -> loader.get());
    }

    //Replace these two with actual logger
    private static void logImageCacheStats() {
        var stats = IMAGE_CACHE.stats();
        System.out.println("[Image Cache] hits=" + stats.hitCount() +
                ", misses=" + stats.missCount() +
                ", hitRate=" + String.format("%.2f", stats.hitRate() * 100) + "%" +
                ", evictions=" + stats.evictionCount());
    }

    private static void logDominantColorCache() {
        var stats = DOMINANT_COLOR_CACHE.stats();
        System.out.println("[Dominant Color Cache] hits=" + stats.hitCount() +
                ", misses=" + stats.missCount() +
                ", hitRate=" + String.format("%.2f", stats.hitRate() * 100) + "%" +
                ", evictions=" + stats.evictionCount());
    }
}
