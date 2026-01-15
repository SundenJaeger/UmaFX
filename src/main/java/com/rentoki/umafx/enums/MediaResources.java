package com.rentoki.umafx.enums;

import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.util.Cache;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum MediaResources {
    APP_ICON("/com/rentoki/umafx/media/temp.png"),
    FALLBACK_ALBUM_ART("/com/rentoki/umafx/media/album-cover-temp.png"),
    METADATA_BANNERS(
            "/com/rentoki/umafx/media/metadata-banner.png",
            "/com/rentoki/umafx/media/metadata-banner2.png"
    );

    private static final Random RANDOM = new Random();
    private final String[] imagePaths;

    MediaResources(String... imagePaths) {
        this.imagePaths = imagePaths;
    }

    public URL getURL() {
        return getURL(0);
    }

    public URL getURL(int index) {
        return MediaResources.class.getResource(imagePaths[index]);
    }

    public Image getImage() {
        return getImage(0);
    }

    public Image getRandomImage() {
        int index = RANDOM.nextInt(imagePaths.length);
        return getImage(index);
    }

    public int size() {
        return imagePaths.length;
    }

    public Image getImage(int index) {
        String path = imagePaths[index];
        return Cache.getOrCompute(path, () -> {
            try {
                URL url = MediaResources.class.getResource(path);
                if (url == null) {
                    throw new MediaResourcesException("Resource not found: " + path);
                }
                return new Image(url.openStream());
            } catch (IOException e) {
                throw new MediaResourcesException("Cannot load image: " + e.getMessage(), e);
            }
        });
    }
}
