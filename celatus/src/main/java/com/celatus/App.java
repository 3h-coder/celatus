package com.celatus;

import java.io.IOException;
import java.security.Key;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.controller.BaseWindowController;
import com.celatus.controller.PopupWindowController;
import com.celatus.util.FXMLUtils;

/**
 * JavaFX App
 */
public class App extends Application {

    // region =====Application Variables=====

    private static final Logger _logger = LogManager.getLogger(App.class.getName());
    
    private static Key key; // 256 bits AES key used to encrypt the db.clts file

    private static Scene scene; // The current app scene

    private static PasswordsDatabase passwordsDatabase;

    private static int originalDatabaseHash; // used to check unsaved modifications

    private static BaseWindowController controller; // controller associated with the current scene

    private static Map<String, ?> tmpVariables; // used to store any variable at runtime

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

    // endregion

    // region =====Main Methods=====

    @Override
    public void init(){
       // Nothing for now
    }

    @Override
    public void start(Stage stage) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Code to be executed when the application is shutting down
            // This can include cleanup tasks or saving data
            try{
                DatabaseHandler.concealDatabase();
            } catch(IOException ex) {
                _logger.error("An unexpected error occured while trying to conceal the passwords database: " + ex.getMessage());
            }
            _logger.info("--------------------Application Shutdown--------------------");
            
        }));
        
        if (DatabaseHandler.dbFileExists()) {
            launchWindow(stage, "entryWindow");
        } else {
            launchWindow(stage, "setupWindow");
        }

        // launchWindow(stage, "mainWindow");
             
    }

    public static void main(String[] args) {
        launch();
    }

    // endregion

    // region =====Secondary Methods=====

    public static void launchWindow(String fxml) throws IOException {
        Map<String, Object> map = FXMLUtils.getSceneAndController(fxml);
        App.scene = (Scene) map.get("Scene");
        App.controller = (BaseWindowController) map.get("Controller");

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show(); 
    }

    public static void launchWindow(Stage stage, String fxml) throws IOException {
        Map<String, Object> map = FXMLUtils.getSceneAndController(fxml);
        App.scene = (Scene) map.get("Scene");
        App.controller = (BaseWindowController) map.get("Controller");

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show(); 
    }

    /**
     * Switches the application's scene to another
     * @param fxml The view to switch to
     * @throws IOException
     */ //  /!\ Do not use!
    public static void setView(String fxml) throws IOException {
        scene.setRoot(FXMLUtils.loadFXML(fxml));
    }

    /**
     * Logs the provided error message and displays it into a pop-up window
     * @param error The error message
     */
    public static void error(Stage window, String error, Logger logger) {
        if (logger != null) {
            logger.error(error);
        }
        try {
            Map<String, Object> map = FXMLUtils.getSceneAndController("popupWindow");
            Scene scene = (Scene) map.get("Scene");
            PopupWindowController controller = (PopupWindowController) map.get("Controller");
            
            Stage errorStage = new Stage();
            errorStage.initModality(Modality.APPLICATION_MODAL);
            errorStage.initStyle(StageStyle.UNDECORATED);
            errorStage.initOwner(window);
            errorStage.setScene(scene);
            controller.setMessage(error);
            errorStage.showAndWait();
        } catch (IOException ex) {
            if (logger != null) {
                 logger.error("Failed to popup the window: " + ex.getMessage());
            } else {
                 _logger.error("Failed to popup the window: " + ex.getMessage());
            }
        }
        
    }

    // endregion
}