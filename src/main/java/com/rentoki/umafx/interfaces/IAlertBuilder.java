package com.rentoki.umafx.interfaces;

import java.util.Optional;

public interface IAlertBuilder<T> {
    T text(String title, String header, String content);

    default T text(String title, String content) {
        return text(title, null, content);
    }

    Optional<?> showAndWait();

    void show();
}
