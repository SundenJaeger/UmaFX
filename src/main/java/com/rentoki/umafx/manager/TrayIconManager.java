package com.rentoki.umafx.manager;

import com.rentoki.umafx.enums.MediaResources;
import com.rentoki.umafx.service.WindowPreferencesService;
import com.rentoki.umafx.util.MenuItemFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import systemtrayfx.core.*;

public class TrayIconManager {
    private SystemTrayFX systemTrayFX;

    private final Stage stage;
    private final MediaPlayerManager mediaPlayerManager;
    private final WindowPreferencesService windowPreferencesService;
    private final Runnable openTrackQueue;

    private final BooleanProperty volumeVisible = new SimpleBooleanProperty(false);

    public TrayIconManager(Stage stage,
                           MediaPlayerManager mediaPlayerManager,
                           WindowPreferencesService windowPreferencesService,
                           Runnable openTrackQueue) {
        this.stage = stage;
        this.mediaPlayerManager = mediaPlayerManager;
        this.windowPreferencesService = windowPreferencesService;
        this.openTrackQueue = openTrackQueue;

        systemTrayFX = createTrayIcon();
    }

    /* ---------------- Properties ---------------- */

    public BooleanProperty volumeVisibleProperty() {
        return volumeVisible;
    }

    /* ---------------- Internals ---------------- */

    private SystemTrayFX createTrayIcon() {
        systemTrayFX = new SystemTrayFX(stage, "UmaFX", MediaResources.APP_ICON.getImage());

        systemTrayFX.addEntry(MenuItemFactory.createTrayEntries(
                stage,
                mediaPlayerManager,
                windowPreferencesService,
                openTrackQueue,
                volumeVisible
        ).toArray(TrayMenuItem[]::new));

        return systemTrayFX;
    }
}