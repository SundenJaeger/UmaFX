package com.rentoki.umafx.interfaces;

public interface PreferencesRepository {
    void putDouble(String key, double value);

    double getDouble(String key, double def);

    void clear();
}
