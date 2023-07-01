module com.celatus {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens com.celatus to javafx.fxml;
    exports com.celatus;
}
