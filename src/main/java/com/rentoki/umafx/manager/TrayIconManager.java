package com.rentoki.umafx.manager;

import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.enums.PlaybackState;
import com.rentoki.umafx.exceptions.EmptySongListException;
import com.rentoki.umafx.exceptions.MediaPlayerException;
import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.util.ShowAlert;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import systemtrayfx.core.*;

public class TrayIconManager {
    private SystemTrayFX systemTrayFX;

    private final Stage stage;
    private final MediaPlayerManager mediaPlayerManager;
    private final Runnable openTrackQueue;
    private final WindowPositionSaver windowPositionSaver;

    private final BooleanProperty volumeVisible = new SimpleBooleanProperty(false);

    public TrayIconManager(Stage stage, MediaPlayerManager mediaPlayerManager, Runnable openTrackQueue, WindowPositionSaver windowPositionSaver) {
        this.stage = stage;
        this.mediaPlayerManager = mediaPlayerManager;
        this.openTrackQueue = openTrackQueue;
        this.windowPositionSaver = windowPositionSaver;

        systemTrayFX = createTrayIcon();
    }

    /* ---------------- Public API ---------------- */

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

    private SystemTrayFX createTrayIcon() {
        systemTrayFX = new SystemTrayFX(stage, "UmaFX", MediaResources.APP_ICON.getImage());

        TrayMenuItem resetWindowItem = new TrayMenuItem("Reset Window");
        resetWindowItem.setOnAction(event -> resetWindow());

        TrayMenuItem openTrackQueueItem = new TrayMenuItem("Open Track Queue");
        openTrackQueueItem.setOnAction(event -> openTrackQueue.run());

        systemTrayFX.addEntry(
                resetWindowItem,
                openTrackQueueItem,
                new Separator(),
                new FXMenuItemWrapper(playPauseItem()),
                new FXMenuItemWrapper(stopItem()),
                new FXMenuItemWrapper(skipItem()),
                new FXMenuItemWrapper(volumeItem()),
                new Separator(),
                new TrayExitMenuItem("Exit UmaFX")
        );

        return systemTrayFX;
    }

    private void runSafe(Runnable action) {
        try {
            action.run();
        } catch (EmptySongListException e) {
            ShowAlert.error().text("Error", e.getMessage())
                    .showAndWait()
                    .ifPresent(buttonType -> openTrackQueue.run());
        } catch (MediaPlayerException e) {
            ShowAlert.showError(e.getMessage());
        }
    }
}