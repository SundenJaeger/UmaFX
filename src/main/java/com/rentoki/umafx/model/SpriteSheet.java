package com.rentoki.umafx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;

public class SpriteSheet {
    public String name;
    public String file;
    public int columns;
    public int rows;
    public int totalFrames;
    public int frameWidth;
    public int frameHeight;

    @JsonIgnore
    public Image image;
}