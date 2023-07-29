package com.celatus;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class FXMLUtils {

    // region =====ListView=====

    @FXML
    public static void adjustListViewHeight(ListView listView) {
        double cellSize = listView.getFixedCellSize();
        listView.setPrefHeight(listView.getItems().size() * cellSize + 2);
    }   

    @FXML
    public static void addToListView(ListView listView, String element) {
       listView.getItems().add(element);
       adjustListViewHeight(listView);
    }

    @FXML
    public static void removeFromListView(ListView listView, String element) {
        listView.getItems().remove(element);
        adjustListViewHeight(listView);
    }
    
    // endregion
}
