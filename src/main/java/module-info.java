module com.rentoki.umafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires com.dustinredmond.fxtrayicon;
    requires javafx.base;


    opens com.rentoki.umafx to javafx.fxml;
    exports com.rentoki.umafx;
    exports com.rentoki.umafx.controller;
    exports com.rentoki.umafx.model;
    exports com.rentoki.umafx.service;
    exports com.rentoki.umafx.interfaces;
    exports com.rentoki.umafx.manager;
    opens com.rentoki.umafx.controller to javafx.fxml;
}