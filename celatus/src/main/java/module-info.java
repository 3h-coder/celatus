module com.celatus {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.celatus to javafx.fxml;
    exports com.celatus;
}
