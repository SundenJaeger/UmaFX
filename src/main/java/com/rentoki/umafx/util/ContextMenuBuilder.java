package com.rentoki.umafx.util;

import com.rentoki.umafx.interfaces.ActionMenuBuilder;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuBuilder implements ActionMenuBuilder<ContextMenuBuilder> {
    private final List<MenuItem> menuItems = new ArrayList<>();

    @Override
    public ContextMenuBuilder addMenuItem(String text, Node graphic, Runnable action) {
        MenuItem item = new MenuItem(text, graphic);
        item.setOnAction(event -> action.run());
        menuItems.add(item);

        return this;
    }

    @Override
    public ContextMenuBuilder addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);

        return this;
    }

    @Override
    public ContextMenuBuilder addSeparator() {
        menuItems.add(new SeparatorMenuItem());

        return this;
    }

    public ContextMenu build() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItems);

        return contextMenu;
    }
}
