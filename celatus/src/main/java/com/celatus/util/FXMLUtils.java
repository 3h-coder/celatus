package com.celatus.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import com.celatus.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class FXMLUtils {

    // region =====ListView=====

    @FXML
    public static void adjustListViewHeight(ListView<String> listView) {
        double cellSize = listView.getFixedCellSize();
        listView.setPrefHeight(listView.getItems().size() * cellSize + 2);
    }   

    @FXML
    public static void addToListView(ListView<String> listView, String element) {
       listView.getItems().add(element);
       adjustListViewHeight(listView);
    }

    @FXML
    public static void removeFromListView(ListView<String> listView, String element) {
        listView.getItems().remove(element);
        adjustListViewHeight(listView);
    }

    @FXML
    public static void updateListView(ListView<String> listView, String element, String newValue) {
        Collections.replaceAll(listView.getItems(), element, newValue);
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

    // region =====Stage / Scene=====

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

    // region =====FXML files=====

    public static FXMLLoader getLoader(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = getLoader(fxml);
        return fxmlLoader.load();
    }

    public static Scene getScene(String fxml) throws IOException {
        return new Scene(loadFXML(fxml));
    }

    public static <T> T getController(String fxml) throws IOException {
        return getLoader(fxml).getController();
    }

    public static Map<String, Object> getSceneAndController(String fxml) throws IOException {
        Map<String, Object> map = new HashMap<>();
        FXMLLoader fxmlLoader = getLoader(fxml);
        map.put("Scene", new Scene(fxmlLoader.load()));
        map.put("Controller", fxmlLoader.getController());
        return map;
    }
    
    public static void launchDialogWindow(Stage owner, String fxml) throws IOException {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initOwner(owner);
        dialogStage.setScene(new Scene(FXMLUtils.loadFXML(fxml)));
        dialogStage.showAndWait();    
    }

    public static void launchDialogWindow(Stage owner, Scene scene) throws IOException {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initOwner(owner);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();    
    }



    // endregion

}
