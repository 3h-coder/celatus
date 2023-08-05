package com.celatus.util;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


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

    // region =====Pane / Stage / Scene=====

    public static void addDarkOverlay(Scene scene) {
        AnchorPane root = (AnchorPane) scene.getRoot();
        Pane overlayPane = new Pane();
        overlayPane.setId("overlayPane");
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Add the overlay pane as a direct child of the root AnchorPane
        root.getChildren().add(overlayPane);

        // Make the overlay pane cover the entire window
        AnchorPane.setTopAnchor(overlayPane, 0.0);
        AnchorPane.setBottomAnchor(overlayPane, 0.0);
        AnchorPane.setLeftAnchor(overlayPane, 0.0);
        AnchorPane.setRightAnchor(overlayPane, 0.0);
    }

    public static void removeDarkOverlay(Scene scene) {
        AnchorPane root = (AnchorPane) scene.getRoot();
        var children = root.getChildren();
        for (var child : children) {
            if ("overlayPane".equals(child.getId())) {
                children.remove(child);
                return;
            }
        }
    }

    // endregion
}
