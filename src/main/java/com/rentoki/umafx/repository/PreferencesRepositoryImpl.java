package com.rentoki.umafx.repository;

import com.rentoki.umafx.exceptions.PreferencesRepositoryException;
import com.rentoki.umafx.interfaces.PreferencesRepository;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesRepositoryImpl implements PreferencesRepository {
    private final Preferences preferences;

    public PreferencesRepositoryImpl(Class<?> clazz) {
        this.preferences = Preferences.userNodeForPackage(clazz);
    }

    @Override
    public void putDouble(String key, double value) {
        try {
            preferences.putDouble(key, value);
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new PreferencesRepositoryException(String.format("Failed to save preference [key=%s, value=%s]", key, value), e);
        }
    }

    @Override
    public double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }

    @Override
    public void clear() {
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            throw new PreferencesRepositoryException("Failed to clear preference", e);
        }
    }
}
