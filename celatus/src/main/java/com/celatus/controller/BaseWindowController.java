package com.celatus.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.enums.AlertMode;
import com.celatus.enums.UserSettings;
import com.celatus.enums.WindowType;
import com.celatus.util.FXMLUtils;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

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
  protected AnchorPane rootPane;
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
    Platform.runLater(
        () -> {
          window = getCurrentWindow();
          scene = window.getScene();
          makeLabelsSelectable();
          enableAnchorPaneFocusOnClick();
          setOnCloseRequest();
          setIcon();
          setWindowTitle("Celatus Password Manager");
        });
  }

  public void setIcon() {
    this.window.getIcons().add(new Image(App.class.getResourceAsStream("images/logo.png")));
  }

  public void setWindowTitle(String title) {
    window.setTitle(title);
  }

  @FXML
  public Stage getCurrentWindow() {
    return (Stage) rootPane.getScene().getWindow();
  }

  @FXML
  public void loadTheme() {
    String theme = appProperties.getProperty(UserSettings.THEME.toString());
    rootPane.getStylesheets().clear();
    rootPane
        .getStylesheets()
        .add(App.class.getResource("styles/" + theme + ".css").toExternalForm());
  }

  /**
   * Launches a window on top of the current one
   *
   * @param windowType : the window you want to launch
   */
  public void launchWindow(WindowType windowType) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(windowType);
      Scene scene = (Scene) map.get("Scene");
      Stage stage = new Stage();
      stage.initOwner(this.window);
      stage.setScene(scene);
      stage.initStyle(StageStyle.UNDECORATED);
      stage.show();
    } catch (Exception ex) {
      App.error(this.window, ex, "An error occured", logger, AlertMode.OK, true);
    }
  }

  public void launchWindow(WindowType windowType, double X, double Y) {
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(windowType);
      Scene scene = (Scene) map.get("Scene");
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
  public void switchAppWindow(WindowType windowType, boolean resizable) {
    try {
      App.launchWindow(windowType, resizable);
      window.close();
    } catch (Exception ex) {
      App.error(
          this.window,
          ex,
          "An unexpected error occured when trying to open" + window,
          logger,
          AlertMode.OK,
          true);
      close();
    }
  }

  /** Switches to the application's main window */
  public void switchToMainWindow() {
    switchAppWindow(WindowType.MAIN, true);
  }

  /**
   * Changes the scene of the current window
   *
   * @param fxml
   */
  public void switchScene(String fxml) {
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
  public void summonNotificationPopup(Stage window, String message) {
    TextArea textArea = new TextArea(message);
    textArea.setWrapText(true);
    textArea.setEditable(false);
    textArea.setMouseTransparent(true);
    textArea.setFocusTraversable(false);
    String theme = App.getProperties().getProperty(UserSettings.THEME.toString());
    textArea.getStylesheets().add(App.class.getResource("styles/" + theme + ".css").toExternalForm());
    textArea.getStyleClass().add("popup");
    FXMLUtils.adjustTextAreaDimensions(textArea);

    Popup popup = new Popup();
    placePopupCorrectly(popup, textArea);

    popup.getContent().addAll(textArea);

    // Create a TranslateTransition to move the popup down
    // right under the menu bar row
    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), popup.getContent().get(0));
    translateTransition.setByY(
        (int) (rowPane1.getHeight() / 2)); // Converting it to int otherwise the text is blurry

    // Create a FadeTransition
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4.9), popup.getContent().get(0));
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setOnFinished(event -> popup.hide());

    // Only one notification popup at a time
    removeRegisteredNotificationPopup();
    App.registerNotificationPopup(popup);

    // Display and animations
    popup.show(window);
    translateTransition.play();
    fadeTransition.play();

    // Store listeners so we can remove them later
    ChangeListener<Number> xListener = (obs, oldX, newX) -> placePopupCorrectly(popup, textArea);
    ChangeListener<Number> yListener = (obs, oldY, newY) -> placePopupCorrectly(popup, textArea);
    ChangeListener<Number> wListener = (obs, oldW, newW) -> placePopupCorrectly(popup, textArea);
    ChangeListener<Number> hListener = (obs, oldH, newH) -> placePopupCorrectly(popup, textArea);
    ChangeListener<Boolean> focusListener = (obs, wasFocused, isNowFocused) -> {
      if (!isNowFocused) {
        popup.hide();
      }
    };

    window.xProperty().addListener(xListener);
    window.yProperty().addListener(yListener);
    window.widthProperty().addListener(wListener);
    window.heightProperty().addListener(hListener);
    window.focusedProperty().addListener(focusListener);

    // Remove all listeners when the popup is hidden
    popup.setOnHidden(e -> {
      window.xProperty().removeListener(xListener);
      window.yProperty().removeListener(yListener);
      window.widthProperty().removeListener(wListener);
      window.heightProperty().removeListener(hListener);
      window.focusedProperty().removeListener(focusListener);
    });
  }

  public boolean notificationPopupShown() {
    Popup popup = App.extractNotificationPopup();
    return popup != null;
  }

  protected void removeRegisteredNotificationPopup() {
    Popup popup = App.extractNotificationPopup();
    if (popup != null) {
      popup.hide();
    }
  }

  private void placePopupCorrectly(Popup popup, TextArea popupText) {
    // The popup is located at the window's top middle
    popup.setX(
        window.getX()
            + (window.getWidth() / 2)
            - (FXMLUtils.computeTextWidth(popupText.getText(), popupText.getFont()) + 2) / 2);
    popup.setY(window.getY());
  }

  /**
   * Browses the window to find all first layer labels (under an anchor pane) and
   * make them
   * selectable upon clicking on them
   */
  private void makeLabelsSelectable() {
    ArrayList<Label> labels = FXMLUtils.getAllNodesByClass(rootPane, Label.class);
    for (Label label : labels) {
      if (!(label.getParent() instanceof AnchorPane)) {
        continue;
      }
      if (label.getStyleClass().contains("badFieldLabel")) {
        continue;
      }
      if (label.getId().equals("warningLabel")) {
        continue;
      }
      AnchorPane parentPane = (AnchorPane) label.getParent();

      label.setOnMouseClicked(
          mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
              if (mouseEvent.getClickCount() == 1) {
                label.setVisible(false);
                TextField textField = new TextField(label.getText());
                textField.setEditable(false);
                textField.setStyle(
                    "-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0 0 0"
                        + " -1;");
                textField.setAlignment(label.getAlignment());
                textField.setPrefWidth(label.getWidth());
                parentPane.getChildren().add(textField);
                AnchorPane.setTopAnchor(textField, AnchorPane.getTopAnchor(label));
                AnchorPane.setBottomAnchor(textField, AnchorPane.getBottomAnchor(label));
                AnchorPane.setLeftAnchor(textField, AnchorPane.getLeftAnchor(label));
                AnchorPane.setRightAnchor(textField, AnchorPane.getRightAnchor(label));
                textField.setLayoutX(label.getLayoutX());

                textField.setOnKeyPressed(
                    event -> {
                      if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE) {
                        parentPane.getChildren().remove(textField);
                        label.setVisible(true);
                      }
                    });
              }
            }
          });
    }
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
  public void windowKeyPressed(KeyEvent event) {
    // Quit program on alt + F4
    if (event.isAltDown() && event.getCode() == KeyCode.F4) {
      this.altF4Detected = true;
    }
  }

  public void setOnCloseRequest() {
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

  @FXML
  public void onMousePressed(MouseEvent event) {
    xOffset = window.getX() - event.getScreenX();
    yOffset = window.getY() - event.getScreenY();
  }

  @FXML
  public void onMouseDragged(MouseEvent event) {
    window.setX(event.getScreenX() + xOffset);
    window.setY(event.getScreenY() + yOffset);
  }

  public void close() {
    window.close();
    if (Stage.getWindows().stream().filter(Window::isShowing).count() == 0) {
      // No more visible windows, exit the app
      App.exit();
    }
  }

  /** Currently not used */
  public void minimize() {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
  }

  /** Currently not used */
  public void maximize() {
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    double minX = primaryScreenBounds.getMinX();
    double minY = primaryScreenBounds.getMinY();
    double screenWidth = primaryScreenBounds.getWidth();
    double screenHeight = primaryScreenBounds.getHeight();

    // If the window is already maximized, we set it back to default
    if (window.getWidth() == screenWidth
        && window.getHeight() == screenHeight
        && window.getX() == minX
        && window.getY() == minY) {
      window.setWidth(900);
      window.setHeight(600);
      window.centerOnScreen();
    } else {
      window.setWidth(screenWidth);
      window.setHeight(screenHeight);
      window.centerOnScreen();
    }
  }

  // endregion
}
