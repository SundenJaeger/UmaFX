package com.rentoki.umafx;

import com.rentoki.umafx.enums.ErrorHeaders;
import com.rentoki.umafx.util.ShowAlert;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

public class Launcher {
    private static FileLock fileLock;
    private static FileChannel fileChannel;

    public static void main(String[] args) {

        if (!lock()) {
            Platform.startup(() -> ShowAlert.showWarning("Another instance is already running."));
            return;
        }

        Application.launch(MainApplication.class, args);
    }

    @SuppressWarnings("resource")
    private static boolean lock() {
        Path path = Path.of(System.getProperty("java.io.tmpdir"), "app.lock");

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            fileChannel = new RandomAccessFile(path.toFile(), "rw").getChannel();
            fileLock = fileChannel.tryLock();

            if (fileLock == null) {
                fileChannel.close();
                return false;
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    fileLock.release();
                    fileChannel.close();
                } catch (IOException e) {
                    ShowAlert.showError(ErrorHeaders.GENERAL_ERROR.getMessage(), e.getMessage());
                }
            }));

            return true;
        } catch (IOException e) {
            ShowAlert.showError(ErrorHeaders.GENERAL_ERROR.getMessage(), e.getMessage());
            return false;
        }
    }
}
