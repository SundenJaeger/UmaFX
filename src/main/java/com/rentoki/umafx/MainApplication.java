package com.rentoki.umafx;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.rentoki.umafx.controller.JukeboxController;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.View;
import com.rentoki.umafx.exceptions.EmptySongListException;
import com.rentoki.umafx.exceptions.MediaPlayerException;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.interfaces.PreferencesRepository;
import com.rentoki.umafx.interfaces.PropertiesRepository;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.repository.PreferencesRepositoryImpl;
import com.rentoki.umafx.repository.PropertiesRepositoryImpl;
import com.rentoki.umafx.service.CharacterPropertiesService;
import com.rentoki.umafx.service.WindowPreferencesService;
import com.rentoki.umafx.util.ShowAlert;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainApplication extends Application {
    private final CharacterPropertiesService characterPropertiesService;
    private final WindowPreferencesService windowPreferencesService;
    private final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();
    private final BooleanProperty volumeVisible = new SimpleBooleanProperty(false);

    private Stage primaryStage;
    private JukeboxController jukeboxController;

    public MainApplication() {
        PropertiesRepository propertiesRepository = new PropertiesRepositoryImpl();
        this.characterPropertiesService = new CharacterPropertiesService(propertiesRepository);

        PreferencesRepository preferencesRepository = new PreferencesRepositoryImpl(MainApplication.class);
        this.windowPreferencesService = new WindowPreferencesService(preferencesRepository);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.JUKEBOX.getFxmlPath()));
        fxmlLoader.setControllerFactory(param -> {
            if (param == JukeboxController.class) {
                return new JukeboxController(windowPreferencesService, characterPropertiesService, mediaPlayerManager, this);
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
        jukeboxController = fxmlLoader.getController();

        characterPropertiesService.initializeProperties();

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(MainApplication.class.getResource("css/base.css").toExternalForm());

        stage.setTitle("UmaFX");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        try {
            stage.getIcons().add(MediaResources.APP_ICON.getImage(MainApplication.class));
        } catch (MediaResourcesException e) {
            stage.getIcons().clear();
        }

        stage.setX(windowPreferencesService.getPosX());
        stage.setY(windowPreferencesService.getPosY());
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(event -> {
            Platform.setImplicitExit(true);
            Platform.exit();
            System.exit(0);
        });

        setTrayIcon();
    }

    public MenuItem playPauseItem() {
        MenuItem menuItem = new MenuItem();

        menuItem.textProperty().bind(Bindings.when(mediaPlayerManager.playingProperty()).then("Pause").otherwise("Play"));
        menuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));

        menuItem.setOnAction(event -> {
            if (mediaPlayerManager.isPlaying()) {
                safeRun(mediaPlayerManager::pause);
            } else {
                safeRun(mediaPlayerManager::play);
            }
        });

        return menuItem;
    }

    public MenuItem stopItem() {
        MenuItem menuItem = new MenuItem("Stop");

        menuItem.setOnAction(event -> safeRun(mediaPlayerManager::stop));
        menuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));

        return menuItem;
    }

    public MenuItem skipItem() {
        MenuItem menuItem = new MenuItem("Skip");

        menuItem.setOnAction(event -> safeRun(mediaPlayerManager::skip));
        menuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));

        return menuItem;
    }

    public CheckMenuItem volumeItem() {
        CheckMenuItem checkMenuItem = new CheckMenuItem("Volume");

        checkMenuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));
        checkMenuItem.selectedProperty().bindBidirectional(volumeVisible);

        return checkMenuItem;
    }

    public BooleanProperty volumeVisibleProperty() {
        return volumeVisible;
    }

    private void setTrayIcon() {
        FXTrayIcon fxTrayIcon;

        try {
            fxTrayIcon = new FXTrayIcon(primaryStage, MediaResources.APP_ICON.getImage(MainApplication.class));
        } catch (MediaResourcesException e) {
            fxTrayIcon = new FXTrayIcon(primaryStage);
        }

        fxTrayIcon.addMenuItem("Reset Window", event -> {
            primaryStage.setX(0);
            primaryStage.setY(0);

            try {
                windowPreferencesService.savePos(0, 0);
            } catch (PreferencesRepositoryException e) {
                ShowAlert.showError(e.getMessage());
            }
        });
        fxTrayIcon.insertSeparator(1);
        fxTrayIcon.addMenuItem("Open Song Queue", event -> jukeboxController.openSongQueue());
        fxTrayIcon.addSeparator();
        fxTrayIcon.addMenuItem(playPauseItem());
        fxTrayIcon.addMenuItem(stopItem());
        fxTrayIcon.addMenuItem(skipItem());
        fxTrayIcon.addMenuItem(volumeItem());
        fxTrayIcon.addSeparator();
        fxTrayIcon.addExitItem("Exit UmaFX");
        fxTrayIcon.show();
    }

    private void safeRun(RunnableWithException action) {
        try {
            action.run();
        } catch (EmptySongListException e) {
            ShowAlert.error().text("Error", e.getMessage()).showAndWait().ifPresent(buttonType -> jukeboxController.openSongQueue());
        } catch (MediaPlayerException e) {
            ShowAlert.showError(e.getMessage());
        }
    }

    @FunctionalInterface
    private interface RunnableWithException {
        void run() throws MediaPlayerException, EmptySongListException;
    }
}