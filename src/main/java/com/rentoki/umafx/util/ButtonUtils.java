package com.rentoki.umafx.util;

import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public final class ButtonUtils {
    private ButtonUtils() {
    }

    public static void round(Button button, double arc) {
        Rectangle rectangle = new Rectangle();
        rectangle.setArcWidth(arc);
        rectangle.setArcHeight(arc);
        rectangle.widthProperty().bind(button.widthProperty());
        rectangle.heightProperty().bind(button.heightProperty());

        button.setShape(rectangle);
    }
}
