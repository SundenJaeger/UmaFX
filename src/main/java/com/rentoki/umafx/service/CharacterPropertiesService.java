package com.rentoki.umafx.service;

import com.rentoki.umafx.exceptions.InvalidCharacterFolderException;
import com.rentoki.umafx.exceptions.PropertiesRepositoryException;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CharacterPropertiesService {
    private static final String PROPERTIES_DIR = System.getProperty("user.home") + "/Documents/UmaFX";
    private static final Path PROPERTIES_PATH = Path.of(PROPERTIES_DIR, "config.properties");
    private static final String SPRITE_SHEET_DIR = System.getProperty("user.home") + "/Documents/UmaFX/spritesheets";

    //Keys
    private static final String CHARACTER = "Character";
    private static final String VERSION = "Version";

    private final PropertiesRepository propertiesRepository;
    private final StringProperty character = new SimpleStringProperty();
    private final int version = 1;

    public CharacterPropertiesService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public void initializeProperties() {
        try {
            Files.createDirectories(Path.of(PROPERTIES_DIR));

            if (!Files.exists(PROPERTIES_PATH)) {
                createProperties();
                return;
            }

            try (FileInputStream fis = new FileInputStream(PROPERTIES_PATH.toFile())) {
                propertiesRepository.load(fis);

                String versionStr = propertiesRepository.getProperty(VERSION);
                if (versionStr == null || !isVersionCompatible(versionStr)) {
                    System.err.println("Properties version mismatch or missing. Recreating properties file");
                    createProperties();
                    return;
                }

                character.set(propertiesRepository.getProperty(CHARACTER));

            }
        } catch (IOException | PropertiesRepositoryException e) {
            System.err.println(e.getMessage());
            createProperties();
        }
    }

    public StringProperty characterProperty() {
        return character;
    }

    public void setCharacter(String characterName) {
        try {
            propertiesRepository.setProperty(CHARACTER, characterName);

            try (FileOutputStream fos = new FileOutputStream(PROPERTIES_PATH.toFile())) {
                propertiesRepository.store(fos);
            }

            character.set(characterName);
        } catch (IOException | PropertiesRepositoryException e) {
            System.err.println("Failed to save character preference: " + e.getMessage());
        }
    }

    public List<String> getAvailableCharacters() {
        Path spriteSheetDir = Path.of(SPRITE_SHEET_DIR);
        List<String> characters = new ArrayList<>();

        try (DirectoryStream<Path> entry = Files.newDirectoryStream(spriteSheetDir)) {
            for (Path path : entry) {
                if (Files.isDirectory(path) && hasValidConfig(path)) {
                    characters.add(path.getFileName().toString());
                } else if (Files.isDirectory(path)) {
                    System.err.println("Skipping invalid character folder (missing config.json): " + path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            throw new InvalidCharacterFolderException(e);
        }

        return characters;
    }

    private void createProperties() {
        try {
            propertiesRepository.setProperty(VERSION, String.valueOf(version));
            propertiesRepository.setProperty(CHARACTER, "teio");

            try (FileOutputStream fos = new FileOutputStream(PROPERTIES_PATH.toFile())) {
                propertiesRepository.store(fos);
            }

            character.set("teio");
        } catch (IOException | PropertiesRepositoryException e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean isVersionCompatible(String versionStr) {
        try {
            int propertiesVersion = Integer.parseInt(versionStr);

            return propertiesVersion == version;
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private boolean hasValidConfig(Path characterPath) {
        Path configFile = characterPath.resolve("config.json");
        return Files.exists(configFile) && Files.isRegularFile(configFile);
    }
}