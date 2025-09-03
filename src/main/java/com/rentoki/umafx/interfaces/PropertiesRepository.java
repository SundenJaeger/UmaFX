package com.rentoki.umafx.interfaces;

import com.rentoki.umafx.exceptions.PropertiesRepositoryException;

import java.io.InputStream;
import java.io.OutputStream;

public interface PropertiesRepository {
    boolean setProperty(String key, String value);

    String getProperty(String key);

    void load(InputStream in) throws PropertiesRepositoryException;

    void store(OutputStream out, String comments) throws PropertiesRepositoryException;

    default void store(OutputStream out) throws PropertiesRepositoryException {
        store(out, "");
    }
}
