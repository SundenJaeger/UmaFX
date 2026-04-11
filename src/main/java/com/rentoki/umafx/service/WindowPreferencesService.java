package com.rentoki.umafx.service;

import com.rentoki.umafx.exceptions.WindowPreferencesException;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class WindowPreferencesService {
    private static final String WINDOW_POS_X = "window_pos_x";
    private static final String WINDOW_POS_Y = "window_pos_y";

    private final Preferences preferences;

    public WindowPreferencesService(Class<?> clazz) {
        preferences = Preferences.userNodeForPackage(clazz);
    }

    public void savePos(double posX, double posY) {
        savePosX(posX);
        savePosY(posY);
    }

    public void savePosX(double posX) {
        try {
            preferences.putDouble(WINDOW_POS_X, posX);
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new WindowPreferencesException(String.format("Failed to save position X [value=%s]", posX), e);
        }
    }

    public void savePosY(double posY) {
        try {
            preferences.putDouble(WINDOW_POS_Y, posY);
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new WindowPreferencesException(String.format("Failed to save position Y [value=%s]", posY), e);
        }
    }

    public void resetPos() {
        savePos(0, 0);
    }

    public double getPosX() {
        return preferences.getDouble(WINDOW_POS_X, 0);
    }

    public double getPosY() {
        return preferences.getDouble(WINDOW_POS_Y, 0);
    }
}
