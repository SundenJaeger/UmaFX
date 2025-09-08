package com.rentoki.umafx.model;

import java.nio.file.Path;

public record Song(Path path) {
    @Override
    public String toString() {
        return path.getFileName().toString();
    }
}
