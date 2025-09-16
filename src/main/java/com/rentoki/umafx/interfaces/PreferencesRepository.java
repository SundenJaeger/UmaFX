package com.rentoki.umafx.interfaces;

import com.rentoki.umafx.exceptions.PreferencesRepositoryException;

public interface PreferencesRepository {
    void putDouble(String key, double value) throws PreferencesRepositoryException;

    double getDouble(String key, double def);

    void clear() throws PreferencesRepositoryException;
}
