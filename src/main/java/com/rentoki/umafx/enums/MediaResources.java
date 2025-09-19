package com.rentoki.umafx.enums;

import com.rentoki.umafx.exceptions.MediaResourcesException;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public enum MediaResources {
    APP_ICON("/com/rentoki/umafx/media/temp.png");

    private final String imagePath;

    MediaResources(String imagePath) {
        this.imagePath = imagePath;
    }

    public URL getURL(Class<?> clazz) {
        return clazz.getResource(imagePath);
    }

    public Image getImage(Class<?> clazz) throws MediaResourcesException {
        try {
            return new Image(getURL(clazz).openStream());
        } catch (IOException e) {
            throw new MediaResourcesException("Cannot load image: " + e.getMessage(), e);
        }
    }
}
