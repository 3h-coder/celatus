module com.celatus {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.celatus to javafx.fxml;
    exports com.celatus;
}
