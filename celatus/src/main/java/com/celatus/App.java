package com.celatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.constants.Constants;
import com.celatus.controller.AlertWindowController;
import com.celatus.controller.BaseWindowController;
import com.celatus.enums.AlertMode;
import com.celatus.enums.AppTempVariable;
import com.celatus.enums.WindowType;
import com.celatus.handler.DatabaseHandler;
import com.celatus.handler.PropertyHandler;
import com.celatus.models.ActionTracker;
import com.celatus.models.CustomUncaughtExceptionHandler;
import com.celatus.models.PasswordsDatabase;
import com.celatus.util.FXMLUtils;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** JavaFX App */
public class App extends Application {

  // region =====Application Variables=====

  private static final Logger _logger = LogManager.getLogger(App.class.getName());

  private static Key key; // 256 bits AES key used to encrypt the db.clts file

  private static Scene scene; // The current app scene

  private static PasswordsDatabase passwordsDatabase;

  private static int originalDatabaseHash; // used to check unsaved modifications

  private static BaseWindowController controller; // controller associated with the app scene

  private static Map<String, Object> tmpVariables; // used to store any variable at runtime, such as signals
  // -> (signals are boolean variables representing a signal sent from one window
  // to the whole application)

  private static ActionTracker actionTracker; // action tracker used for ctrl+Z and ctrl+Y

  private static Properties properties; // App and user properties

  private static HostServices hostServices; // Used to open up the password's website

  // endregion

  // region =====Getters and Setters=====

  public static Key getKey() {
    return key;
  }

  public static void setKey(Key value) {
    key = value;
  }

  public static PasswordsDatabase getPasswordsDatabase() {
    return passwordsDatabase;
  }

  public static void setPasswordsDatabase(PasswordsDatabase value) {
    passwordsDatabase = value;
  }

  public static int getOriginalDatabaseHash() {
    return originalDatabaseHash;
  }

  public static void setOriginalDatabaseHash(int value) {
    originalDatabaseHash = value;
  }

  public static Scene getScene() {
    return scene;
  }

  public static Stage getWindow() {
    return (Stage) scene.getWindow();
  }

  public static BaseWindowController getController() {
    return controller;
  }

  public static void setController(BaseWindowController controller) {
    App.controller = controller;
  }

  public static Map<String, ?> getTmpVariables() {
    return tmpVariables;
  }

  public static ActionTracker getActionTracker() {
    return actionTracker;
  }

  public static Properties getProperties() {
    return properties;
  }

  public static HostServices getHS() {
    return hostServices;
  }

  // endregion

  // region =====Main Methods=====

  @Override
  public void init() {
    Platform.setImplicitExit(false);
  }

  @Override
  public void start(Stage stage) throws IOException {

    setShutdownHook();
    initVariables();
    loadProperties();

    if (DatabaseHandler.dbFileExists()) {
      launchWindow(stage, WindowType.ENTRY, false);
    } else {
      launchWindow(stage, WindowType.SETUP, false);
    }
  }

  public static void exit() {
    _logger.info("--------------------Application Shutdown--------------------");
    Platform.exit();
    System.exit(0);
  }

  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
    launch();
  }

  // endregion

  // region =====Secondary Methods=====

  public void loadProperties() {
    if (!PropertyHandler.propertyFileExists()) {
      PropertyHandler.createDefaultProperties();
    }
    properties = PropertyHandler.readProperties();
    PropertyHandler.checkProperties(properties);
  }

  /**
   * Launches a window, making it the application's top layer current window.
   *
   * @throws IOException
   */
  public static void launchWindow(WindowType windowType, boolean resizable) throws IOException {
    Map<String, Object> map = FXMLUtils.getSceneAndController(windowType);
    App.scene = (Scene) map.get(Constants.SCENE);
    App.controller = (BaseWindowController) map.get(Constants.CONTROLLER);

    Stage stage = new Stage();
    stage.setScene(scene);
    stage.setResizable(resizable);
    stage.initStyle(StageStyle.DECORATED);
    stage.show();
  }

  /**
   * Launches a window, making it the application's top layer current window.
   * 
   * @throws IOException
   */
  public static void launchWindow(Stage stage, WindowType windowType, boolean resizable) throws IOException {
    Map<String, Object> map = FXMLUtils.getSceneAndController(windowType);
    App.scene = (Scene) map.get(Constants.SCENE);
    App.controller = (BaseWindowController) map.get(Constants.CONTROLLER);

    stage.setScene(scene);
    stage.setResizable(resizable);
    stage.initStyle(StageStyle.DECORATED);
    stage.show();
  }

  /**
   * Switches the application's scene to another
   *
   * @param fxml The view to switch to
   * @throws IOException
   */
  // /!\ Do not use! Currently not working properly
  public static void setView(String fxml) throws IOException {
    scene.setRoot(FXMLUtils.loadFXML(fxml));
  }

  /**
   * Logs the provided error message and displays it into a pop-up window
   *
   * @param error The error message
   */
  public static void error(
      Stage window,
      Throwable error,
      String errorMessage,
      Logger logger,
      AlertMode mode,
      boolean printStackTrace) {
    // Logging the error (and eventually the stack trace)
    errorMessage = errorMessage + " : " + error;
    if (logger != null) {
      logger.error("To user -> " + errorMessage);
      if (printStackTrace) {
        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        logger.error("Full stack trace: " + sw.toString());
      }
    }
    // Opening a popup window to notify the user
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(WindowType.POPUP);
      Scene scene = (Scene) map.get(Constants.SCENE);
      AlertWindowController controller = (AlertWindowController) map.get(Constants.CONTROLLER);

      Stage errorStage = new Stage();
      errorStage.initModality(Modality.APPLICATION_MODAL);
      errorStage.initStyle(StageStyle.UNDECORATED);
      errorStage.initOwner(window);
      errorStage.setScene(scene);
      controller.setMessage(errorMessage);
      controller.setErrorIcon();
      controller.setMode(mode);
      errorStage.showAndWait();
    } catch (IOException ex) {
      if (logger != null) {
        logger.error("Failed to alert the window: " + ex.getMessage());
      } else {
        _logger.error("Failed to alert the window: " + ex.getMessage());
      }
    }
  }

  /**
   * Logs the provided warning message and displays it into a pop-up window
   *
   * @param warning The warning message
   */
  public static void warn(Stage window, String warning, Logger logger, AlertMode mode) {
    // Logging the warning
    if (logger != null) {
      logger.warn("To user -> " + warning);
    }
    // Opening a popup window to notify the user
    try {
      Map<String, Object> map = FXMLUtils.getSceneAndController(WindowType.POPUP);
      Scene scene = (Scene) map.get(Constants.SCENE);
      AlertWindowController controller = (AlertWindowController) map.get(Constants.CONTROLLER);

      Stage errorStage = new Stage();
      errorStage.initModality(Modality.APPLICATION_MODAL);
      errorStage.initStyle(StageStyle.UNDECORATED);
      errorStage.initOwner(window);
      errorStage.setScene(scene);
      controller.setMessage(warning);
      controller.setMode(mode);
      errorStage.showAndWait();
    } catch (IOException ex) {
      if (logger != null) {
        logger.error("Failed to alert the window: " + ex.getMessage());
      } else {
        _logger.error("Failed to alert the window: " + ex.getMessage());
      }
    }
  }

  public static void addTempVariable(AppTempVariable tempVariable, Object value) {
    if (tmpVariables == null) {
      tmpVariables = new HashMap<>();
    }
    tmpVariables.put(tempVariable.toString(), value);
  }

  public static void removeTempVariable(AppTempVariable tempVariable) {
    if (tmpVariables != null) {
      tmpVariables.remove(tempVariable.toString());
    }
  }

  public static void clearTempVariables() {
    if (tmpVariables != null) {
      tmpVariables.clear();
    }
  }

  /**
   * Overall it's better to use extract instead of get since they're meant to be
   * temporary and deleted asap
   */
  public static Object getTempVariable(AppTempVariable tempVariable) {
    if (tmpVariables != null) {
      return tmpVariables.get(tempVariable.toString());
    }
    return null;
  }

  /**
   * @param tempVariable
   * @return The temp variable, removing it from the internal map along the way.
   */
  public static Object extractTempVariable(AppTempVariable tempVariable) {
    var key = tempVariable.toString();
    if (tmpVariables != null && tmpVariables.containsKey(key)) {
      var variable = tmpVariables.get(key);
      tmpVariables.remove(key);
      return variable;
    }
    return null;
  }

  /**
   * 
   * @param signal
   * @return
   */
  public static boolean getSignal(AppTempVariable signal) {
    return getSignal(signal.toString());
  }

  /**
   * Retrieves a signal, removing it from the temporary variables map
   *
   * @param signalKey
   * @return
   */
  public static boolean getSignal(String signalKey) {
    if (tmpVariables != null && tmpVariables.containsKey(signalKey)) {
      tmpVariables.remove(signalKey);
      return true;
    }
    return false;
  }

  public static void registerNotificationPopup(Popup popup) {
    addTempVariable(AppTempVariable.NOTIFICATION_POPUP, popup);
  }

  public static Popup getNotificationPopup() {
    return (Popup) getTempVariable(AppTempVariable.NOTIFICATION_POPUP);
  }

  public static Popup extractNotificationPopup() {
    return (Popup) extractTempVariable(AppTempVariable.NOTIFICATION_POPUP);
  }

  /**
   * Saves a property
   *
   * @param key
   * @param value
   */
  public static void saveProperty(String key, String value) {
    properties.setProperty(key, value);
    PropertyHandler.writeProperties(properties);
  }

  // endregion

  // region =====Private Methods=====

  private void initVariables() {
    actionTracker = new ActionTracker();
    hostServices = getHostServices();
  }

  private void setShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  // Code to be executed when the application is shutting down
                  // This can include cleanup tasks or saving data
                  try {
                    DatabaseHandler.concealDatabase();
                  } catch (IOException ex) {
                    _logger.error(
                        "An unexpected error occured while trying to conceal the passwords"
                            + " database: "
                            + ex.getMessage());
                  }
                  _logger.info("--------------------Application Shutdown--------------------");
                }));
  }

  // endregion

}
