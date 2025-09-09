package com.rentoki.umafx.interfaces;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public interface ActionMenuBuilder<T> {
    T addMenuItem(String text, Node graphic, EventHandler<ActionEvent> action);

    default T addMenuItem(String text, EventHandler<ActionEvent> action) {
        return addMenuItem(text, null, action);
    }

    T addSeparator();
}
