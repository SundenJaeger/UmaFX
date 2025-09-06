package com.rentoki.umafx.controller;

import com.rentoki.umafx.manager.AnimationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class JukeboxController {
    private final AnimationManager animationManager = new AnimationManager();
    private final StringProperty characterName = new SimpleStringProperty();

    @FXML
    private ImageView spriteImageView;

    @FXML
    private void initialize() {
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                animationManager.loadAndStartAnimation(newValue, spriteImageView);
            }
        });
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }
}