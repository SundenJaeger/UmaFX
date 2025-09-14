package com.rentoki.umafx.service;

import com.rentoki.umafx.interfaces.PreferencesRepository;

public class WindowPreferencesService {
    private static final String WINDOW_POS_X = "window_pos_x";
    private static final String WINDOW_POS_Y = "window_pos_y";

    private final PreferencesRepository preferencesRepository;

    public WindowPreferencesService(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    public void savePos(double posX, double posY) {
        savePosX(posX);
        savePosY(posY);
    }

    public void savePosX(double posX) {
        preferencesRepository.putDouble(WINDOW_POS_X, posX);
    }

    public void savePosY(double posY) {
        preferencesRepository.putDouble(WINDOW_POS_Y, posY);
    }

    public double getPosX() {
        return preferencesRepository.getDouble(WINDOW_POS_X, 0);
    }

    public double getPosY() {
        return preferencesRepository.getDouble(WINDOW_POS_Y, 0);
    }
}
