package com.rentoki.umafx;

import com.rentoki.umafx.controller.JukeboxController;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import com.rentoki.umafx.repository.PropertiesRepositoryImpl;
import com.rentoki.umafx.service.CharacterPreferenceService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.JUKEBOX.getFxmlPath()));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        PropertiesRepository repository = new PropertiesRepositoryImpl();
        CharacterPreferenceService preferenceService = new CharacterPreferenceService(repository);
        JukeboxController controller = fxmlLoader.getController();
        controller.characterNameProperty().bind(preferenceService.characterProperty());

        preferenceService.initializeProperties();

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(MainApplication.class.getResource("css/base.css").toExternalForm());

        stage.setTitle("UmaFX");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setAlwaysOnTop(true);
    }
}