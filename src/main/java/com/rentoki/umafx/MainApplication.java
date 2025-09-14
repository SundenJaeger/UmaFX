package com.rentoki.umafx;

import com.rentoki.umafx.controller.JukeboxController;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.interfaces.PreferencesRepository;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.repository.PreferencesRepositoryImpl;
import com.rentoki.umafx.repository.PropertiesRepositoryImpl;
import com.rentoki.umafx.service.CharacterPreferenceService;
import com.rentoki.umafx.service.WindowPreferencesService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainApplication extends Application {
    private final CharacterPreferenceService characterPreferenceService;
    private final WindowPreferencesService windowPreferencesService;
    private final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();

    public MainApplication() {
        PropertiesRepository propertiesRepository = new PropertiesRepositoryImpl();
        this.characterPreferenceService = new CharacterPreferenceService(propertiesRepository);

        PreferencesRepository preferencesRepository = new PreferencesRepositoryImpl(MainApplication.class);
        this.windowPreferencesService = new WindowPreferencesService(preferencesRepository);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.JUKEBOX.getFxmlPath()));
        fxmlLoader.setControllerFactory(param -> {
            if (param == JukeboxController.class) {
                return new JukeboxController(windowPreferencesService, characterPreferenceService, mediaPlayerManager);
            } else {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        characterPreferenceService.initializeProperties();
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(MainApplication.class.getResource("css/base.css").toExternalForm());

        stage.setTitle("UmaFX");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setX(windowPreferencesService.getPosX());
        stage.setY(windowPreferencesService.getPosY());
        stage.setAlwaysOnTop(true);
    }
}