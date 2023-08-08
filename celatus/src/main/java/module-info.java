module com.celatus {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.celatus to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.celatus.controller to javafx.fxml;
    exports com.celatus;
    exports com.celatus.controller;
    exports com.celatus.util;
}
