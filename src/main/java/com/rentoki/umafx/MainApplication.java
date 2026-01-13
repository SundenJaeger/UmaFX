package com.rentoki.umafx;

import com.rentoki.umafx.controller.JukeboxController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.interfaces.PreferencesRepository;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.manager.TrayIconManager;
import com.rentoki.umafx.repository.PreferencesRepositoryImpl;
import com.rentoki.umafx.repository.PropertiesRepositoryImpl;
import com.rentoki.umafx.service.CharacterPropertiesService;
import com.rentoki.umafx.service.WindowPreferencesService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    private static final String APP_TITLE = "UmaFX";

    private final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();
    private TrayIconManager trayIconManager;

    private final CharacterPropertiesService characterPropertiesService;
    private final WindowPreferencesService windowPreferencesService;

    private JukeboxController jukeboxController;

    public MainApplication() {
        PropertiesRepository propertiesRepository = new PropertiesRepositoryImpl();
        this.characterPropertiesService = new CharacterPropertiesService(propertiesRepository);

        PreferencesRepository preferencesRepository = new PreferencesRepositoryImpl(MainApplication.class);
        this.windowPreferencesService = new WindowPreferencesService(preferencesRepository);
    }

    @Override
    public void start(Stage stage) throws IOException {
        initializeTray(stage);
        Scene scene = loadScene();
        configureStage(stage, scene);
        restoreWindowPosition(stage);
        setupShutdown(stage);
        loadFont();
    }

    /* ---------------- Helpers ---------------- */

    private void initializeTray(Stage stage) {
        trayIconManager = new TrayIconManager(stage, mediaPlayerManager, () -> jukeboxController.openSongQueue(), windowPreferencesService::savePos);
        trayIconManager.show();
    }

    private Scene loadScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(View.JUKEBOX.getFxmlPath()));

        loader.setControllerFactory(this::createController);

        Scene scene = new Scene(loader.load(), 320, 240);
        jukeboxController = loader.getController();

        characterPropertiesService.initializeProperties();

        configureScene(scene);

        return scene;
    }

    private Object createController(Class<?> clazz) {
        if (clazz == JukeboxController.class) {
            return new JukeboxController(windowPreferencesService, characterPropertiesService, mediaPlayerManager, trayIconManager);
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create controller: " + clazz, e);
        }
    }

    private void configureScene(Scene scene) {
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css/base.css")).toExternalForm());
    }

    private void configureStage(Stage stage, Scene scene) {
        stage.setTitle(APP_TITLE);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);

        loadStageIcon(stage);

        stage.show();
    }

    private void loadStageIcon(Stage stage) {
        try {
            stage.getIcons().add(MediaResources.APP_ICON.getImage());
        } catch (MediaResourcesException e) {
            stage.getIcons().clear();
        }
    }

    private void restoreWindowPosition(Stage stage) {
        stage.setX(windowPreferencesService.getPosX());
        stage.setY(windowPreferencesService.getPosY());
    }

    private void setupShutdown(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> {
            Platform.setImplicitExit(true);
            Platform.exit();
            System.exit(0);
        });
    }

    private void loadFont() {
        Font font = Font.loadFont(MainApplication.class.getResourceAsStream("fonts/AtlanSemiBold.otf"), 0);
        System.out.println(font.getFamily());
    }
}