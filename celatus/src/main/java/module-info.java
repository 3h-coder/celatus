module com.celatus {
        requires java.desktop;
        requires transitive javafx.controls;
        requires transitive javafx.fxml;
        requires javafx.graphics;
        requires org.apache.commons.lang3;
        requires transitive org.apache.logging.log4j;
        requires com.fasterxml.jackson.core;
        requires com.fasterxml.jackson.databind;
        requires com.fasterxml.jackson.datatype.jsr310;
        requires javafx.base;

        opens com.celatus to
                        javafx.fxml,
                        com.fasterxml.jackson.databind;
        opens com.celatus.controller to
                        javafx.fxml;
        opens com.celatus.models to
                        javafx.fxml,
                        com.fasterxml.jackson.databind;
        opens com.celatus.enums to
                        javafx.fxml;
        opens com.celatus.interfaces to
                        javafx.fxml;

        exports com.celatus;
        exports com.celatus.controller;
        exports com.celatus.util;
        exports com.celatus.models;
        exports com.celatus.interfaces;
        exports com.celatus.enums;
        exports com.celatus.dataclasses;
}
