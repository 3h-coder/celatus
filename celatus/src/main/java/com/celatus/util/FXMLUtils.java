package com.celatus.util;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;


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

    // region =====Context Menu=====

    public static ContextMenu createContextMenu(String[] menuItems) {
        ContextMenu contextMenu = new ContextMenu();
        for (String menuItem : menuItems) {
            MenuItem item = new MenuItem(menuItem);
            contextMenu.getItems().add(item);
        }
        return contextMenu;
    }

    // endregion
}
