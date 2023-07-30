package com.celatus;

import java.io.IOException;
import java.security.Key;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.controller.PopupWindowController;

/**
 * JavaFX App
 */
public class App extends Application {

    // region =====Application Variables=====

    private static final Logger _logger = LogManager.getLogger(App.class.getName());

    private static Scene scene;

    // 256 bits AES key used to encrypt the db.clts file
    private static Key key;

    private static PasswordsDatabase passwordsDatabase;

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

    public static Stage getWindow() {
        return (Stage) scene.getWindow();
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
        Stage stage = new Stage();
        scene = new Scene(loadFXML(fxml));
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show(); 
    }

    public static void launchWindow(Stage stage, String fxml) throws IOException {
        scene = new Scene(loadFXML(fxml));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show(); 
    }

    public static void launchDialogWindow(Stage owner, String fxml) throws IOException {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initOwner(owner);
        dialogStage.setScene(new Scene(loadFXML(fxml)));
        dialogStage.showAndWait();
    }

    /**
     * Switches the application's scene to another
     * @param fxml The view to switch to
     * @throws IOException
     */
    public static void setView(String fxml) throws IOException {
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
    public static void error(Stage window, String error, Logger logger) {
        if (logger != null) {
            logger.error(error);
        }
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("popupWindow.fxml"));
            Parent root = loader.load();
            PopupWindowController controller = loader.getController();

            Stage errorStage = new Stage();
            errorStage.initModality(Modality.APPLICATION_MODAL);
            errorStage.initStyle(StageStyle.UNDECORATED);
            errorStage.initOwner(window);
            errorStage.setScene(new Scene(root));
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