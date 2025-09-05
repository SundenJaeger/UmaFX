package com.rentoki.umafx.service;

import com.rentoki.umafx.exceptions.PropertiesRepositoryException;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

enum Key {
    CHARACTER("Character"),
    VERSION("Version");

    private final String key;

    Key(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

public class CharacterPreferenceService {
    private static final String PROPERTIES_DIR = System.getProperty("user.home") + "/Documents/UmaFX";
    private static final Path PROPERTIES_PATH = Path.of(PROPERTIES_DIR, "config.properties");

    private final PropertiesRepository propertiesRepository;
    private final StringProperty character = new SimpleStringProperty();
    private final int version = 1;

    public CharacterPreferenceService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public void initializeProperties() {
        try {
            Files.createDirectory(Path.of(PROPERTIES_DIR));

            if (!Files.exists(PROPERTIES_PATH)) {
                createProperties();
                return;
            }

            try (FileInputStream fis = new FileInputStream(PROPERTIES_PATH.toFile())) {
                propertiesRepository.load(fis);

                String versionStr = propertiesRepository.getProperty(Key.VERSION.getKey());
                if (versionStr == null || !isVersionCompatible(versionStr)) {
                    System.err.println("Properties version mismatch or missing. Recreating properties file");
                    createProperties();
                    return;
                }

                character.set(propertiesRepository.getProperty(Key.CHARACTER.getKey()));

            }
        } catch (IOException | PropertiesRepositoryException e) {
            System.err.println(e.getMessage());
            createProperties();
        }
    }

    public StringProperty characterProperty() {
        return character;
    }

    private void createProperties() {
        try {
            propertiesRepository.setProperty(Key.VERSION.getKey(), String.valueOf(version));
            propertiesRepository.setProperty(Key.CHARACTER.getKey(), "teio");

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
}