package com.rentoki.umafx.util;

import com.rentoki.umafx.dialog.TrackPropertiesDialog;
import com.rentoki.umafx.enums.ErrorHeaders;
import com.rentoki.umafx.enums.PlaybackState;
import com.rentoki.umafx.exceptions.DesktopActionException;
import com.rentoki.umafx.exceptions.WindowPreferencesException;
import com.rentoki.umafx.manager.MediaPlayerManager;
import com.rentoki.umafx.model.Track;
import com.rentoki.umafx.service.WindowPreferencesService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.stage.Stage;
import systemtrayfx.core.FXMenuItemWrapper;
import systemtrayfx.core.Separator;
import systemtrayfx.core.TrayExitMenuItem;
import systemtrayfx.core.TrayMenuItem;

import java.util.List;

public class MenuItemFactory {
    private static final String OPEN_TRACK_QUEUE_TEXT = "Open Track Queue";
    private static final String PLAY_TEXT = "Play";
    private static final String PAUSE_TEXT = "Pause";
    private static final String STOP_TEXT = "Stop";
    private static final String SKIP_TEXT = "Skip";
    private static final String VOLUME_TEXT = "Volume";
    private static final String RESET_WINDOW_TEXT = "Reset Window";
    private static final String EXIT_TEXT = "Exit UmaFX";
    private static final String OPEN_FILE_LOCATION_TEXT = "Open File Location";
    private static final String PROPERTIES_TEXT = "Properties";
    private static final String REMOVE_TEXT = "Remove";

    public static ContextMenu createJukeboxContextMenu(MediaPlayerManager mediaPlayerManager,
                                                       Runnable openTrackQueue,
                                                       BooleanProperty volumeVisible) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                openTrackQueueItem(openTrackQueue),
                new SeparatorMenuItem(),
                playPauseItem(mediaPlayerManager, openTrackQueue),
                stopItem(mediaPlayerManager),
                skipItem(mediaPlayerManager),
                volumeItem(mediaPlayerManager, volumeVisible),
                new SeparatorMenuItem(),
                exitItem()
        );

        return contextMenu;
    }

    public static List<TrayMenuItem> createTrayEntries(Stage stage,
                                                       MediaPlayerManager mediaPlayerManager,
                                                       WindowPreferencesService windowPreferencesService,
                                                       Runnable openTrackQueue,
                                                       BooleanProperty volumeVisible) {
        TrayMenuItem resetWindow = new TrayMenuItem(RESET_WINDOW_TEXT);
        resetWindow.setOnAction(event -> {
            try {
                windowPreferencesService.resetPos();
                stage.setX(0);
                stage.setY(0);
            } catch (WindowPreferencesException e) {
                ShowAlert.showError(e.getMessage());
            }
        });

        return List.of(
                resetWindow,
                new FXMenuItemWrapper(openTrackQueueItem(openTrackQueue)),
                new Separator(),
                new FXMenuItemWrapper(playPauseItem(mediaPlayerManager, openTrackQueue)),
                new FXMenuItemWrapper(stopItem(mediaPlayerManager)),
                new FXMenuItemWrapper(skipItem(mediaPlayerManager)),
                new FXMenuItemWrapper(volumeItem(mediaPlayerManager, volumeVisible)),
                new Separator(),
                new TrayExitMenuItem(EXIT_TEXT)
        );
    }

    public static ContextMenu createTrackListCellContextMenu(ListView<Track> trackListView, Runnable removeTrack) {
        MenuItem removeTrackItem = new MenuItem(REMOVE_TEXT);
        removeTrackItem.setOnAction(event -> removeTrack.run());

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                openFileLocation(trackListView),
                new SeparatorMenuItem(),
                removeTrackItem,
                new SeparatorMenuItem(),
                openTrackProperties(trackListView)
        );

        return contextMenu;
    }

    private static MenuItem openTrackQueueItem(Runnable runnable) {
        MenuItem menuItem = new MenuItem(OPEN_TRACK_QUEUE_TEXT);
        menuItem.setOnAction(event -> runnable.run());

        return menuItem;
    }

    private static MenuItem playPauseItem(MediaPlayerManager mediaPlayerManager, Runnable openTrackQueue) {
        MenuItem menuItem = new MenuItem();

        menuItem.textProperty().bind(Bindings.createStringBinding(() -> mediaPlayerManager.getState() == PlaybackState.PLAYING ? PAUSE_TEXT : PLAY_TEXT, mediaPlayerManager.stateProperty())
        );

        menuItem.disableProperty().bind(mediaPlayerManager.playProperty().not()
                .and(mediaPlayerManager.pauseProperty().not()));

        menuItem.setOnAction(event -> {
            switch (mediaPlayerManager.getState()) {
                case PLAYING -> mediaPlayerManager.pause();
                case PAUSED, STOPPED -> mediaPlayerManager.play();
                default -> {
                }
            }
        });

        return menuItem;
    }

    private static MenuItem stopItem(MediaPlayerManager mediaPlayerManager) {
        MenuItem menuItem = new MenuItem(STOP_TEXT);

        menuItem.disableProperty().bind(mediaPlayerManager.stopProperty().not());

        menuItem.setOnAction(event -> mediaPlayerManager.stop());

        return menuItem;
    }

    private static MenuItem skipItem(MediaPlayerManager mediaPlayerManager) {
        MenuItem menuItem = new MenuItem(SKIP_TEXT);

        menuItem.disableProperty().bind(mediaPlayerManager.skipProperty().not());

        menuItem.setOnAction(event -> mediaPlayerManager.skip());

        return menuItem;
    }

    private static CheckMenuItem volumeItem(MediaPlayerManager mediaPlayerManager, BooleanProperty volumeVisible) {
        CheckMenuItem checkMenuItem = new CheckMenuItem(VOLUME_TEXT);

        checkMenuItem.disableProperty().bind(Bindings.isEmpty(mediaPlayerManager.getSongs()));
        checkMenuItem.selectedProperty().bindBidirectional(volumeVisible);

        return checkMenuItem;
    }

    private static MenuItem exitItem() {
        MenuItem menuItem = new MenuItem(EXIT_TEXT);
        menuItem.setOnAction(event -> Platform.exit());

        return menuItem;
    }

    private static MenuItem openFileLocation(ListView<Track> trackListView) {
        MenuItem menuItem = new MenuItem(OPEN_FILE_LOCATION_TEXT);

        menuItem.setOnAction(event -> {
            try {
                Track selectedTrack = trackListView.getSelectionModel().getSelectedItem();
                DesktopAction.openFileLocation(selectedTrack.getPath().toFile());
            } catch (DesktopActionException e) {
                ShowAlert.showError(ErrorHeaders.GENERAL_ERROR.getMessage(), e.getMessage());
            }
        });
        menuItem.disableProperty().bind(Bindings.size(trackListView.getSelectionModel().getSelectedItems()).greaterThan(1));

        return menuItem;
    }

    private static MenuItem openTrackProperties(ListView<Track> trackListView) {
        MenuItem menuItem = new MenuItem(PROPERTIES_TEXT);

        menuItem.setOnAction(event -> {
            Track selectedTrack = trackListView.getSelectionModel().getSelectedItem();
            new TrackPropertiesDialog(selectedTrack).show();
        });

        return menuItem;
    }
}
