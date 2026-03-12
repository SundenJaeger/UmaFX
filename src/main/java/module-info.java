module com.rentoki.umafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires javafx.media;
    requires javafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires jaudiotagger;
    requires javafx.swing;
    requires net.coobird.thumbnailator;
    requires color.thief;
    requires com.github.benmanes.caffeine;
    requires systemtrayfx.core;

    opens com.rentoki.umafx to javafx.fxml;
    exports com.rentoki.umafx;
    exports com.rentoki.umafx.controller;
    exports com.rentoki.umafx.model;
    exports com.rentoki.umafx.service;
    exports com.rentoki.umafx.interfaces;
    exports com.rentoki.umafx.exceptions;
    exports com.rentoki.umafx.manager;
    exports com.rentoki.umafx.enums;
    exports com.rentoki.umafx.dialog;
    opens com.rentoki.umafx.controller to javafx.fxml;
}