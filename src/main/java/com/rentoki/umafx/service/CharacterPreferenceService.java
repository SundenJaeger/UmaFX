package com.rentoki.umafx.service;

import com.rentoki.umafx.enums.View;
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
    private static final Path PREFERENCE_DIR = Path.of(System.getProperty("user.dir"), "config.properties");

    private final PropertiesRepository propertiesRepository;
    private final StringProperty character = new SimpleStringProperty();

    public CharacterPreferenceService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public void initializeProperties() {
        if (!Files.exists(PREFERENCE_DIR)) {
            createProperties();
        } else {
            try (FileInputStream fis = new FileInputStream(PREFERENCE_DIR.toFile())) {
                propertiesRepository.load(fis);

                character.set(propertiesRepository.getProperty(CHARACTER_KEY));
            } catch (IOException | PropertiesRepositoryException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public StringProperty characterProperty() {
        return character;
    }

    private void createProperties() {
        propertiesRepository.setProperty(CHARACTER_KEY, "teio");

        try (FileOutputStream fos = new FileOutputStream(PREFERENCE_DIR.toFile())) {
            propertiesRepository.store(fos);
        } catch (IOException | PropertiesRepositoryException e) {
            System.err.println(e.getMessage());
        }
    }
}