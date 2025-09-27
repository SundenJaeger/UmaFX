package com.rentoki.umafx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentoki.umafx.model.SpriteSheet;
import com.rentoki.umafx.model.SpriteSheetConfig;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SpriteLoader {
    private static final String SPRITE_SHEET_DIR = System.getProperty("user.home") + "/Documents/UmaFX/spritesheets";

    private final ObjectMapper mapper = new ObjectMapper();

    public List<SpriteSheet> loadSpriteSheets(String folder) throws IOException {
        Files.createDirectories(Path.of(SPRITE_SHEET_DIR));
        Path configPath = Path.of(SPRITE_SHEET_DIR, folder, "config.json");

        SpriteSheetConfig config = mapper.readValue(configPath.toFile(), SpriteSheetConfig.class);

        for (SpriteSheet sheet : config.sheets) {
            Path imagePath = Path.of(SPRITE_SHEET_DIR, folder, sheet.file);
            sheet.image = new Image(imagePath.toFile().toURI().toString());
        }

        return config.sheets;
    }
}