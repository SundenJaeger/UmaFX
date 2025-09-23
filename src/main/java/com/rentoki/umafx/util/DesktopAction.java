package com.rentoki.umafx.util;

import com.rentoki.umafx.exceptions.DesktopActionException;

import java.io.File;
import java.io.IOException;

public final class DesktopAction {
    private DesktopAction() {
    }

    public static void openFileLocation(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new DesktopActionException("File path is null.");
        }

        File file = new File(filePath);
        openFileLocation(file);
    }

    public static void openFileLocation(File file) {
        if (file == null || !file.exists()) {
            throw new DesktopActionException("File doesn't exist.");
        }

        try {
            new ProcessBuilder("explorer", "/select,", file.getAbsolutePath()).start();
        } catch (IOException e) {
            throw new DesktopActionException("Failed to open file: " + file.getAbsolutePath(), e);
        }
    }
}
