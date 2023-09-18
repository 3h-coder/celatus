module com.celatus {
  requires java.desktop;
  requires javafx.controls;
  requires javafx.fxml;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.datatype.jsr310;

  opens com.celatus to
      javafx.fxml,
      com.fasterxml.jackson.databind;
  opens com.celatus.controller to
      javafx.fxml;

  exports com.celatus;
  exports com.celatus.controller;
  exports com.celatus.util;
}
