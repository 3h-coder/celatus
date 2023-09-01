package com.celatus.util;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import com.celatus.App;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class FXMLUtils {

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    // region =====General components=====

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        getAllChildren(root, nodes);
        return nodes;
    }

    public static void getAllChildren(Parent parent, ArrayList<Node> nodes) {
        for (var child : parent.getChildrenUnmodifiable()) {
            nodes.add(child);
            if (child instanceof Parent) {
                getAllChildren((Parent)child, nodes);
            }   
        }
    }

    public static ArrayList<Node> getAllNodesByClass(Parent root, Class<?> nodeClass) {
        ArrayList<Node> nodes = getAllNodes(root);
        return filterNodesByClass(nodes, nodeClass);
    }

    public static ArrayList<Node> filterNodesByClass(ArrayList<Node> nodes, Class<?> clazz) {
        ArrayList<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (clazz.isInstance(node)) {
                result.add(node);
            }
        }
        return result;
    }

    public static void setMaxWidth(double maxWidth, javafx.scene.layout.Region... regions) {
        for (var region : regions) {
            region.setMaxWidth(maxWidth);
        } 
    }

    public static void setMinWidth(double minWidth, javafx.scene.layout.Region... regions) {
        for (var region : regions) {
            region.setMinWidth(minWidth);
        } 
    }

    // endregion

    // region =====Table Column=====

    public static void setMaxWidth(double maxWidth, TableColumn... columns) {
        for (var column : columns) {
            column.setMaxWidth(maxWidth);
        } 
    }

    public static void setMinWidth(double minWidth, TableColumn... columns) {
        for (var column : columns) {
            column.setMinWidth(minWidth);
        } 
    }

    // endregion

    // region =====Text=====

    public static double computeTextHeight(String text, javafx.scene.text.Font font, double width) {
        javafx.scene.text.Text helper = new javafx.scene.text.Text();
        helper.setFont(font);
        helper.setText(text);
        helper.setWrappingWidth(width);
        return helper.getLayoutBounds().getHeight();
    }

    public static double computeTextWidth(String text, javafx.scene.text.Font font) {
        javafx.scene.text.Text helper = new javafx.scene.text.Text();
        helper.setFont(font);
        helper.setText(text);
        return helper.getLayoutBounds().getWidth() * 1.018;
    }

    // endregion

    // region =====TextArea=====

    public static void adjustTextAreaHeight(TextArea textArea) {
        final double MIN_HEIGHT = 30.0;
        final double LINE_HEIGHT = 19.0; // Minimum height to ensure readability

        double width = textArea.getWidth();
        double textwidth = computeTextWidth(textArea.getText(), textArea.getFont());
        /*logger.debug("width: " + width);
        logger.debug("textwidth: " + textwidth);
        logger.debug("textWidth / width: " + (textwidth / width));*/

        int linesNeeded = (int)Math.ceil(textwidth / width);
        //logger.debug("Lines needed: " + linesNeeded);
        textArea.setPrefHeight(MIN_HEIGHT + (linesNeeded-1) * LINE_HEIGHT);
        //logger.debug("Height set to : " + linesNeeded * LINE_HEIGHT);
        // Force a layout update by invalidating the layout
        if (textArea.getParent() != null) {
            textArea.getParent().requestLayout();
        }   
    }

    public static void adjustTextAreaDimensions(TextArea textArea) {
        final double MIN_HEIGHT = 30.0;
        final double LINE_HEIGHT = 19.0; // Minimum height to ensure readability
        final double MAX_WIDTH = 300.0;

        textArea.setMaxWidth(MAX_WIDTH);

        double textwidth = computeTextWidth(textArea.getText(), textArea.getFont());
        // logger.debug("textwidth: " + textwidth);
        int linesNeeded = (int)Math.ceil(textwidth / MAX_WIDTH);
        // logger.debug("linesNeeded: " + linesNeeded);

        if (linesNeeded == 1) {
            textArea.setMaxWidth(textwidth + 15.0);
        }

        textArea.setPrefHeight(MIN_HEIGHT + (linesNeeded-1) * LINE_HEIGHT);
        textArea.setMinHeight(textArea.getPrefHeight());
    }

    // endregion 

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

    // region =====TableView=====

    @FXML
    public static <T> void adjustTableViewHeight(TableView<T> tableView) {
        double cellSize = tableView.getFixedCellSize();
        tableView.setPrefHeight((tableView.getItems().size() * cellSize + 2) + 24.0);
    }

    @FXML
    public static <T> void addToTableView(TableView<T> tableView, T element) {
        tableView.getItems().add(element);
        adjustTableViewHeight(tableView);
    }

    // endregion

    // region =====Menu / Context Menu / Menu Items=====

    public static ContextMenu createContextMenu(String[] menuItems) {
        ContextMenu contextMenu = new ContextMenu();
        for (String menuItem : menuItems) {
            MenuItem item = new MenuItem(menuItem);
            contextMenu.getItems().add(item);
        }
        return contextMenu;
    }

    public static ArrayList<MenuItem> createMenuItems(String[] menuItems) {
        ArrayList<MenuItem> result = new ArrayList<>();

        for (String menuItem : menuItems) {
            MenuItem item = new MenuItem(menuItem);
            result.add(item);
        }
        return result;
    }

    public static void disableAll(ContextMenu contextMenu) {
        for (var item : contextMenu.getItems()) {
            item.setDisable(true);
        }
    }

    public static void enableAll(ContextMenu contextMenu) {
        for (var item : contextMenu.getItems()) {
            item.setDisable(false);
        }
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

    public static void summonPopup(Stage window, String message) {

        TextArea textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMouseTransparent(true);
        textArea.getStylesheets().add(App.class.getResource("styles/default.css").toExternalForm());
        adjustTextAreaDimensions(textArea);
        
        Popup popup = new Popup();

        // The popup is located at the window's middle
        popup.setX(window.getX() + window.getWidth() / 2);
        popup.setY(window.getY());

        popup.getContent().addAll(textArea);
        popup.setAutoHide(true);

        // Create a TranslateTransition to move the popup down right under the menu bar row
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), popup.getContent().get(0));
        translateTransition.setByY(28);

        // Create a FadeTransition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4.9), popup.getContent().get(0));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(event -> popup.hide());

        popup.show(window);
        translateTransition.play();
        fadeTransition.play();
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
