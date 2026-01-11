package com.rentoki.umafx.manager;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.PlaybackState;
import com.rentoki.umafx.exceptions.EmptySongListException;
import com.rentoki.umafx.exceptions.MediaPlayerException;
import com.rentoki.umafx.exceptions.MediaResourcesException;
import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.util.ShowAlert;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class TrayIconManager {
    private FXTrayIcon fxTrayIcon;

    private final Stage stage;
    private final MediaPlayerManager mediaPlayerManager;
    private final Runnable openSongQueue;
    private final WindowPositionSaver windowPositionSaver;

    private final BooleanProperty volumeVisible = new SimpleBooleanProperty(false);

    public TrayIconManager(Stage stage, MediaPlayerManager mediaPlayerManager, Runnable openSongQueue, WindowPositionSaver windowPositionSaver) {
        this.stage = stage;
        this.mediaPlayerManager = mediaPlayerManager;
        this.openSongQueue = openSongQueue;
        this.windowPositionSaver = windowPositionSaver;
    }

    /* ---------------- Public API ---------------- */

    public void show() {
        fxTrayIcon = createTrayIcon();
        configureMenu();
        fxTrayIcon.show();
    }

    public void hide() {
        if (fxTrayIcon != null) {
            fxTrayIcon.hide();
        }
    }

    public void resetWindow() {
        stage.setX(0);
        stage.setY(0);

        try {
            windowPositionSaver.save(0, 0);
        } catch (PreferencesRepositoryException e) {
            ShowAlert.showError(e.getMessage());
        }
    }

    public MenuItem playPauseItem() {
        MenuItem menuItem = new MenuItem();

        menuItem.textProperty().bind(Bindings.createStringBinding(() -> mediaPlayerManager.getState() == PlaybackState.PLAYING ? "Pause" : "Play", mediaPlayerManager.stateProperty())
        );

        menuItem.disableProperty().bind(mediaPlayerManager.playProperty().not()
                .and(mediaPlayerManager.pauseProperty().not()));

        menuItem.setOnAction(event -> runSafe(() -> {
            switch (mediaPlayerManager.getState()) {
                case PLAYING -> mediaPlayerManager.pause();
                case PAUSED, STOPPED -> mediaPlayerManager.play();
                default -> {
                }
            }
        }));

        return menuItem;
    }

    public MenuItem stopItem() {
        MenuItem menuItem = new MenuItem("Stop");

        menuItem.disableProperty().bind(mediaPlayerManager.stopProperty().not());

        menuItem.setOnAction(event -> runSafe(mediaPlayerManager::stop));

        return menuItem;
    }

    public MenuItem skipItem() {
        MenuItem menuItem = new MenuItem("Skip");

        menuItem.disableProperty().bind(mediaPlayerManager.skipProperty().not());

        menuItem.setOnAction(event -> runSafe(mediaPlayerManager::skip));

        return menuItem;
    }

    public CheckMenuItem volumeItem() {
        CheckMenuItem checkMenuItem = new CheckMenuItem("Volume");

        checkMenuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));
        checkMenuItem.selectedProperty().bindBidirectional(volumeVisible);

        return checkMenuItem;
    }

    @FunctionalInterface
    public interface WindowPositionSaver {
        void save(double x, double y) throws PreferencesRepositoryException;
    }

    /* ---------------- Properties ---------------- */

    public BooleanProperty volumeVisibleProperty() {
        return volumeVisible;
    }

    /* ---------------- Internals ---------------- */

    private FXTrayIcon createTrayIcon() {
        try {
            return new FXTrayIcon(stage, MediaResources.APP_ICON.getImage());
        } catch (MediaResourcesException e) {
            return new FXTrayIcon(stage);
        }
    }

    private void configureMenu() {
        fxTrayIcon.addMenuItem("Reset Window", event -> resetWindow());

        fxTrayIcon.insertSeparator(1);
        fxTrayIcon.addMenuItem("Open Song Queue", event -> openSongQueue.run());

        fxTrayIcon.addSeparator();
        fxTrayIcon.addMenuItem(playPauseItem());
        fxTrayIcon.addMenuItem(stopItem());
        fxTrayIcon.addMenuItem(skipItem());
        fxTrayIcon.addMenuItem(volumeItem());

        fxTrayIcon.addSeparator();
        fxTrayIcon.addExitItem("Exit UmaFX");
    }

    private void runSafe(Runnable action) {
        try {
            action.run();
        } catch (EmptySongListException e) {
            ShowAlert.error().text("Error", e.getMessage())
                    .showAndWait()
                    .ifPresent(buttonType -> openSongQueue.run());
        } catch (MediaPlayerException e) {
            ShowAlert.showError(e.getMessage());
        }
    }
}