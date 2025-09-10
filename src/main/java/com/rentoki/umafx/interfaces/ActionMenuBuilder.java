package com.rentoki.umafx.interfaces;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public interface ActionMenuBuilder<T> {
    T addMenuItem(String text, Node graphic, Runnable action);

    T addMenuItem(MenuItem menuItem);

    default T addMenuItem(String text, Runnable action) {
        return addMenuItem(text, null, action);
    }

    T addSeparator();
}
