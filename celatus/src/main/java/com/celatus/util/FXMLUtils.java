package com.celatus.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.constants.Constants;
import com.celatus.dataclasses.ScreenCoordinates;
import com.celatus.enums.WindowType;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLUtils {

  private static final Logger logger = LogManager.getLogger(App.class.getName());

  // region General components

  public static ArrayList<Node> getAllNodes(Parent root) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    getAllChildren(root, nodes);
    return nodes;
  }

  private static void getAllChildren(Parent parent, ArrayList<Node> nodes) {
    for (var child : parent.getChildrenUnmodifiable()) {
      nodes.add(child);
      if (child instanceof Parent) {
        getAllChildren((Parent) child, nodes);
      }
    }
  }

  public static <T> ArrayList<T> getAllNodesByClass(Parent root, Class<T> nodeClass) {
    ArrayList<Node> nodes = getAllNodes(root);
    return filterNodesByClass(nodes, nodeClass);
  }

  private static <T> ArrayList<T> filterNodesByClass(ArrayList<Node> nodes, Class<T> clazz) {
    ArrayList<T> result = new ArrayList<>();
    for (Node node : nodes) {
      if (clazz.isInstance(node)) {
        result.add(clazz.cast(node));
      }
    }
    return result;
  }

  public static void hideAllChildren(Parent parent, Node... exceptions) {
    var children = getAllNodes(parent);
    // Removing all the children of our exceptions from the list
    if (exceptions != null) {
      for (Node element : exceptions) {
        if (!(element instanceof Parent)) {
          continue;
        }
        for (var child : ((Parent) element).getChildrenUnmodifiable()) {
          children.remove(child);
        }
      }
    }
    // Hiding the elements in the list
    for (Node child : children) {
      if (exceptions != null && Arrays.asList(exceptions).contains(child)) {
        continue;
      }
      try {
        child.setVisible(false);
      } catch (Exception ex) {
        logger.debug("Cannot hide the element : " + child);
      }
    }
  }

  public static void showAllChildren(Parent parent, Node... exceptions) {

    var children = getAllNodes(parent);
    // Removing all the children of our exceptions from the list
    if (exceptions != null) {
      for (Node element : exceptions) {
        if (!(element instanceof Parent)) {
          continue;
        }
        for (var child : ((Parent) element).getChildrenUnmodifiable()) {
          children.remove(child);
        }
      }
    }
    // Hiding the elements in the list
    for (Node child : children) {
      if (exceptions != null && Arrays.asList(exceptions).contains(child)) {
        continue;
      }
      try {
        child.setVisible(true);
      } catch (Exception ex) {
        logger.debug("Cannot show the element : " + child);
      }
    }
  }

  public static void hideElements(Node... elements) {
    for (Node element : elements) {
      try {
        element.setVisible(false);
      } catch (Exception ex) {
        logger.debug("Cannot hide the element: " + element);
      }
    }
  }

  public static void collapseElements(Node... elements) {
    for (Node element : elements) {
      try {
        element.setVisible(false);
        element.setManaged(false);
      } catch (Exception ex) {
        logger.debug("Cannot collapse the element: " + element);
      }
    }
  }

  public static void showElements(Node... elements) {
    for (Node element : elements) {
      try {
        element.setVisible(true);
        element.setManaged(true);
      } catch (Exception ex) {
        logger.debug("Cannot show the element: " + element);
      }
    }
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

  // region Table Column

  public static void setMaxWidth(double maxWidth, @SuppressWarnings("rawtypes") TableColumn... columns) {
    for (var column : columns) {
      column.setMaxWidth(maxWidth);
    }
  }

  public static void setMinWidth(double minWidth, @SuppressWarnings("rawtypes") TableColumn... columns) {
    for (var column : columns) {
      column.setMinWidth(minWidth);
    }
  }

  // endregion

  // region Text

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

  // region TextArea / TextField

  public static void adjustTextAreaHeight(TextArea textArea) {
    final double MIN_HEIGHT = 30.0;
    final double LINE_HEIGHT = 19.0; // Minimum height to ensure readability

    double width = textArea.getWidth();
    double textwidth = computeTextWidth(textArea.getText(), textArea.getFont());

    int linesNeeded = (int) Math.ceil(textwidth / width);
    textArea.setPrefHeight(MIN_HEIGHT + (linesNeeded - 1) * LINE_HEIGHT);
    // Force a layout update
    if (textArea.getParent() != null) {
      textArea.getParent().requestLayout();
    }
  }

  public static void adjustTextAreaDimensions(TextArea textArea) {
    final double MIN_HEIGHT = 30.0;
    final double LINE_HEIGHT = 19.0; // Minimum height to ensure readability
    final double MAX_WIDTH = 300.0;

    textArea.setMaxWidth(MAX_WIDTH);

    double textwidth = computeTextWidth(textArea.getText(), textArea.getFont()) + 2;
    int linesNeeded = (int) Math.ceil(textwidth / MAX_WIDTH);

    if (linesNeeded == 1) {
      textArea.setMaxWidth(textwidth + 15.0);
    }

    textArea.setPrefHeight(MIN_HEIGHT + (linesNeeded - 1) * LINE_HEIGHT);
    textArea.setMinHeight(textArea.getPrefHeight());
  }

  // endregion

  // region ListView

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

  @FXML
  public static void moveUp(ListView<String> listView, String element) {
    var list = listView.getItems();
    int index = list.indexOf(element);
    if (index == 0) {
      return;
    }
    String tmp = list.get(index - 1);
    list.set(index - 1, element);
    list.set(index, tmp);
  }

  @FXML
  public static void moveDown(ListView<String> listView, String element) {
    var list = listView.getItems();
    int index = list.indexOf(element);
    if (index == list.size() - 1) {
      return;
    }
    String tmp = list.get(index + 1);
    list.set(index + 1, element);
    list.set(index, tmp);
  }

  // endregion

  // region TableView

  @FXML
  public static <T> void adjustTableViewHeight(TableView<T> tableView) {
    double cellSize = tableView.getFixedCellSize();
    tableView.setPrefHeight((tableView.getItems().size() * cellSize + 2) + cellSize);
  }

  @FXML
  public static <T> void addToTableView(TableView<T> tableView, T element) {
    tableView.getItems().add(element);
    adjustTableViewHeight(tableView);
  }

  // endregion

  // region Menu / Context Menu / Menu Items

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

  // region Stage / Scene

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

  /**
   * Calculates the coordinates to spawn the new window on the owner window's
   * right while making it
   * stay within the screen bounds.
   *
   * @param primaryStage    : The "owner" window
   * @param radius          : The maximum range near the primary stage
   * @param newWindowWidth  : The width of the window we want to spawn
   * @param newWindowHeight : The height of the window we want to spawn
   * @return
   */
  public static ScreenCoordinates findOuterCoordinatesForWindow(
      Stage primaryStage, double radius, double newWindowWidth, double newWindowHeight) {
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    double newX = Math.min(
        primaryStage.getX() + primaryStage.getWidth() + Math.random() * radius,
        screenWidth - newWindowWidth - Math.random() * radius);
    double newY = Math.min(
        primaryStage.getY() + Math.random() * radius,
        screenHeight - newWindowHeight - Math.random() * radius);

    return new ScreenCoordinates((int) newX, (int) newY);
  }

  public static ScreenCoordinates findCenter(Stage stage) {
    double newX = stage.getX() + (stage.getWidth() / 2);
    double newY = stage.getY() + (stage.getHeight() / 2);

    return new ScreenCoordinates((int) newX, (int) newY);
  }

  // endregion

  // region FXML files

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

  public static Map<String, Object> getSceneAndController(WindowType windowType) throws IOException {
    Map<String, Object> map = new HashMap<>();
    FXMLLoader fxmlLoader = getLoader(windowType.getName());
    map.put(Constants.SCENE, new Scene(fxmlLoader.load()));
    map.put(Constants.CONTROLLER, fxmlLoader.getController());
    return map;
  }

  public static void launchDialogWindow(Stage owner, String fxml) throws IOException {
    Stage dialogStage = new Stage();
    dialogStage.setScene(new Scene(FXMLUtils.loadFXML(fxml)));
    prepareDialogWindow(owner, dialogStage);
    dialogStage.showAndWait();
  }

  public static void launchDialogWindow(Stage owner, Scene scene) throws IOException {
    Stage dialogStage = new Stage();
    dialogStage.setScene(scene);
    prepareDialogWindow(owner, dialogStage);
    dialogStage.showAndWait();
  }

  private static void prepareDialogWindow(Stage owner, Stage dialogStage) {
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initStyle(StageStyle.UNDECORATED);
    dialogStage.initOwner(owner);
  }

  // endregion

}
