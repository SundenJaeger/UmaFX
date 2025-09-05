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

public class CharacterPreferenceService {
    private static final String CHARACTER_KEY = "Character";
    private static final String VERSION_KEY = "Version";
    private static final Path PROPERTIES_DIR = Path.of(System.getProperty("user.dir"), "config.properties");

    private final PropertiesRepository propertiesRepository;
    private final StringProperty character = new SimpleStringProperty();
    private final int version = 1;

    public CharacterPreferenceService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public void initializeProperties() {
        if (!Files.exists(PROPERTIES_DIR)) {
            createProperties();
            return;
        }

        try (FileInputStream fis = new FileInputStream(PROPERTIES_DIR.toFile())) {
            propertiesRepository.load(fis);

            String versionStr = propertiesRepository.getProperty(VERSION_KEY);
            if (versionStr == null || !isVersionCompatible(versionStr)) {
                System.err.println("Properties version mismatch or missing. Recreating properties file");
                createProperties();
                return;
            }

            character.set(propertiesRepository.getProperty(CHARACTER_KEY));

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
            propertiesRepository.setProperty(VERSION_KEY, String.valueOf(version));
            propertiesRepository.setProperty(CHARACTER_KEY, "teio");

            try (FileOutputStream fos = new FileOutputStream(PROPERTIES_DIR.toFile())) {
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