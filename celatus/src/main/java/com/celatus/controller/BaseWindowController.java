package com.celatus.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.constants.Constants;
import com.celatus.enums.AlertMode;
import com.celatus.enums.WindowType;
import com.celatus.handler.NotificationHandler;
import com.celatus.util.FXMLUtils;
import com.celatus.util.ThemeUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Mother class of all of our controllers, contains all common behaviour to all
 * window controllers
 */
public abstract class BaseWindowController {

  // region =====Variables=====

  protected final Logger logger = LogManager.getLogger(this.getClass().toString().replace("class ", ""));

  protected Properties appProperties;

  @FXML
  protected ImageView logo;
  @FXML
  protected Pane rootPane;
  @FXML
  protected AnchorPane rowPane1;
  @FXML
  protected Button minimizeButton;
  @FXML
  protected Button closeButton;
  @FXML
  protected Scene scene;
  @FXML
  protected Stage window;

  private static double xOffset = 0;
  private static double yOffset = 0;

  private boolean altF4Detected = false;

  // endregion

  // region =====Window Methods=====

  public void initialize() {
    appProperties = App.getProperties();
    loadTheme();
    // We have to make sure the scene is fully initialized to properly set up our
    // variables
    Platform.runLater(this::lateInitialize);
  }

  /**
   * This method is called after the scene is fully initialized, so we can safely
   * access the window and scene.
   * 
   * It is called inside the initialize method, in a
   * Platform.runLater block to ensure that the scene is ready.
   */
  protected void lateInitialize() {
    scene = getScene();
    window = (Stage) scene.getWindow();

    enableAnchorPaneFocusOnClick();
    setUpCloseRequestHandling();
    setIcon();
    setWindowTitle("Celatus Password Manager");
  }

  protected void loadTheme() {
    rootPane.getStylesheets().clear();
    rootPane.getStylesheets().add(ThemeUtils.getTheme());
  }

  protected void launchChildWindow(WindowType windowType, double X, double Y) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(windowType);
      Scene scene = (Scene) map.get(Constants.SCENE);
      Stage stage = new Stage();
      stage.initOwner(this.window);
      stage.setScene(scene);
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setX(X);
      stage.setY(Y);
      stage.show();
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  /**
   * Switches window with the provided one. In other terms, closes the current
   * window to open the
   * specified one.
   *
   * <p>
   * <b>Note :</b> This will make the new window the App's main window
   *
   * @param windowType : the window (as {@link com.celatus.enums.WindowType})
   */
  protected void switchAppWindow(WindowType windowType, boolean resizable) {
    try {
      App.launchWindow(windowType, resizable);
    } catch (Exception ex) {
      App.error(
          this.window,
          ex,
          "An unexpected error occured when trying to open" + window,
          logger,
          AlertMode.OK,
          true);
    } finally {
      close();
    }
  }

  /** Switches to the application's main window */
  protected void switchToMainWindow() {
    switchAppWindow(WindowType.MAIN, true);
  }

  /**
   * Changes the scene of the current window
   *
   * @param fxml
   */
  protected void switchScene(String fxml) {
    try {
      scene.setRoot(FXMLUtils.loadFXML(fxml));
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  /**
   * Summons a 5 seconds popup on top of the given window
   *
   * @param window
   * @param message
   */
  protected void summonNotificationPopup(Stage window, String message) {
    NotificationHandler.summonNotificationPopup(window, message);
  }

  private Scene getScene() {
    return (Scene) rootPane.getScene();
  }

  private void setIcon() {
    this.window.getIcons().add(new Image(App.class.getResourceAsStream("images/logo.png")));
  }

  private void setWindowTitle(String title) {
    window.setTitle(title);
  }

  /**
   * Sets up all the anchor panes so that they get focus whenever the user clicks
   * on them
   */
  private void enableAnchorPaneFocusOnClick() {
    ArrayList<Node> nodes = FXMLUtils.getAllNodes(rootPane);
    for (Node node : nodes) {
      if (node instanceof AnchorPane && node.getOnMouseClicked() == null) {
        node.setOnMouseClicked(
            event -> {
              node.requestFocus();
            });
      }
    }
  }

  // endregion

  // region =====Event Methods=====

  @FXML
  protected void close() {
    window.close();
  }

  @FXML
  private void minimize() {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
  }

  @FXML
  protected void windowKeyPressed(KeyEvent event) {
    if (event.isAltDown() && event.getCode() == KeyCode.F4) {
      this.altF4Detected = true;
    }
  }

  @FXML
  private void onMousePressed(MouseEvent event) {
    xOffset = window.getX() - event.getScreenX();
    yOffset = window.getY() - event.getScreenY();
  }

  @FXML
  private void onMouseDragged(MouseEvent event) {
    window.setX(event.getScreenX() + xOffset);
    window.setY(event.getScreenY() + yOffset);
  }

  /**
   * Sets up the close request handling for the window.
   */
  private void setUpCloseRequestHandling() {
    window.setOnCloseRequest(
        event -> {
          // The default alt+F4 only closes the foreground window, not the whole app
          if (this.altF4Detected) {
            event.consume();
            App.exit();
          }
          close();
        });
  }

  // endregion
}
