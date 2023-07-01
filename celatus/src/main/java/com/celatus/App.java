package com.celatus;

import java.io.IOException;

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

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    // region =====Application Variables=====

    private static Scene scene;

    // endregion

    // region =====Main Methods=====

    @Override
    public void start(Stage stage) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Code to be executed when the application is shutting down
            // This can include cleanup tasks or saving data
            logger.info("--------------------Application Shutdown--------------------");
        }));

        scene = new Scene(loadFXML("setupWindow"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();      
    }

    public static void main(String[] args) {
        launch();
    }

    // endregion

    // region =====Secondary Methods=====

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

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

    // endregion
}