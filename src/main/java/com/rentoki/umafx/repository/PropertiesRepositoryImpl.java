package com.rentoki.umafx.repository;

import com.rentoki.umafx.exceptions.PropertiesRepositoryException;
import com.rentoki.umafx.interfaces.PropertiesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesRepositoryImpl implements PropertiesRepository {
    private final Properties properties;

    public PropertiesRepositoryImpl() {
        this.properties = new Properties();
    }

    @Override
    public boolean setProperty(String key, String value) {
        properties.setProperty(key, value);
        return true;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void load(InputStream in) throws PropertiesRepositoryException {
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new PropertiesRepositoryException("Cannot load properties.", e);
        }
    }

    @Override
    public void store(OutputStream out, String comments) throws PropertiesRepositoryException {
        try {
            properties.store(out, comments);
        } catch (IOException e) {
            throw new PropertiesRepositoryException("Cannot save properties.", e);
        }
    }
}
