package com.celatus.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Class used for our password entry and category record tables */
public class RecordEntryView {
  private String date;
  private String changes;

  public RecordEntryView(String date, String changes) {
    this.date = date;
    this.changes = changes;
  }

  public String getDate() {
    return date;
  }

  public String getChanges() {
    return changes;
  }

  public StringProperty dateProperty() {
    return new SimpleStringProperty(date);
  }

  public StringProperty changesProperty() {
    return new SimpleStringProperty(changes);
  }
}
