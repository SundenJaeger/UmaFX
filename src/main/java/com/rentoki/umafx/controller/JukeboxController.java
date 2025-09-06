package com.rentoki.umafx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentoki.umafx.model.SpriteSheet;
import com.rentoki.umafx.model.SpriteSheetConfig;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class JukeboxController {
    private static final String SPRITE_SHEET_DIR = System.getProperty("user.home") + "/Documents/UmaFX/spritesheets";
    private static final int FRAMES_PER_SECOND = 30;

    private final StringProperty characterName = new SimpleStringProperty();

    private SpriteSheet currentSheet;
    private int currentFrame;

    @FXML
    private ImageView spriteImageView;

    @FXML
    private void initialize() {
        characterName.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                loadAndStarAnimation(newValue);
            }
        });
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }

    private void loadAndStarAnimation(String characterFolder) {
        List<SpriteSheet> sheets = loadSpriteSheets(characterFolder);
        currentSheet = sheets.getFirst();
        spriteImageView.setImage(currentSheet.image);

        Duration frameDuration = Duration.seconds(1.0 / FRAMES_PER_SECOND);

        Timeline timeline = new Timeline(new KeyFrame(frameDuration, event -> {
            currentFrame = (currentFrame + 1) % currentSheet.totalFrames;
            updateFrame();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private List<SpriteSheet> loadSpriteSheets(String folder) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Files.createDirectories(Path.of(SPRITE_SHEET_DIR));
            Path configPath = Path.of(SPRITE_SHEET_DIR, folder, "config.json");

            SpriteSheetConfig config = mapper.readValue(configPath.toFile(), SpriteSheetConfig.class);

            for (SpriteSheet sheet : config.sheets) {
                Path imagePath = Path.of(SPRITE_SHEET_DIR, folder, sheet.file);
                sheet.image = new Image(imagePath.toFile().toURI().toString());
            }

            return config.sheets;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFrame() {
        int column = currentFrame % currentSheet.columns;
        int row = currentFrame / currentSheet.columns;

        Rectangle2D viewport = new Rectangle2D(
                column * currentSheet.frameWidth,
                row * currentSheet.frameHeight,
                currentSheet.frameWidth,
                currentSheet.frameHeight
        );

        spriteImageView.setViewport(viewport);
    }
}
