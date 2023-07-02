package com.celatus;

import java.io.IOException;
import java.io.File;
import java.security.Key;
import java.util.HashMap;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

    // region =====Application Variables=====

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    private static Scene scene;
    private static final String dbFilePath = "db.clts";

    // 256 bits AES key used to encrypt the db.clts file
    private static Key key;

    private static HashMap<Object, Object> data = new HashMap<Object, Object>();

    // endregion

    // region =====Getters and Setters=====

    public Key getKey() {
        return key;
    }

    public void setKey(Key value) {
        key = value;
    }

    public HashMap<Object, Object> getData() {
        return data;
    }

    public void setData(HashMap<Object, Object> newData) {
        data = newData; 
    }

    // endregion

    // region =====Main Methods=====

    @Override
    public void start(Stage stage) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Code to be executed when the application is shutting down
            // This can include cleanup tasks or saving data
            try{
                if (dbFileExists()) {
                    CryptoUtils.hideFile(dbFilePath, true);
                }
            } catch(IOException ex) {
                logger.error("An unexpected error occured while trying to conceal sensitive files: " + ex.getMessage());
            }
            logger.info("--------------------Application Shutdown--------------------");
            
        }));
        
        if (dbFileExists()) {
            launchWindow(stage, "entryWindow");
        } else {
            launchWindow(stage, "setupWindow");
        }
             
    }

    public static void main(String[] args) {
        launch();
    }

    // endregion

    // region =====Secondary Methods=====

    public static void launchWindow(Stage stage, String fxml) throws IOException {
        scene = new Scene(loadFXML(fxml));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show(); 
    }

    /**
     * Switches the application's scene to another
     * @param fxml The view to switch to
     * @throws IOException
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Logs the provided error message and displays it into a pop-up window
     * @param error The error message
     */
    public static void error(String error) {
        logger.error(error);
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("errorWindow.fxml"));
            Parent root = loader.load();

            ErrorWindowController controller = loader.getController();

            Stage errorStage = new Stage();
            errorStage.initStyle(StageStyle.UNDECORATED);
            errorStage.setScene(new Scene(root));
            errorStage.show();
            controller.setErrorMessage(error);

        } catch (IOException ex) {
            logger.error("Failed to popup the error window: " + ex.getMessage());
        }
        
    }

    /**
     * Checks if the pp.bin file exists in the app's directory
     * @return
     */
    public static boolean dbFileExists() {
        File file = new File(dbFilePath);
        return file.exists();
    }

    // endregion
}