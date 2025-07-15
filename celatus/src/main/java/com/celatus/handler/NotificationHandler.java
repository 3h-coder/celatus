package com.celatus.handler;

import java.util.ArrayList;

import com.celatus.App;
import com.celatus.enums.UserSettings;
import com.celatus.util.FXMLUtils;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Handles notifications within the application.
 */
public class NotificationHandler {

    // region =====Variables=====

    private static Popup notificationPopup;

    // endregion

    // region =====Methods=====

    public static void summonNotificationPopup(Stage window, String message) {
        hideActiveNotificationPopup();

        Popup popup = new Popup();
        var textArea = buildTextArea(message);

        setUpNewPopup(window, popup, textArea);
        showNewPopup(window, popup);
    }

    // endregion

    // region =====Private Methods=====

    private static void hideActiveNotificationPopup() {
        if (notificationPopup != null) {
            notificationPopup.hide();
        }
    }

    private static TextArea buildTextArea(String message) {
        var theme = App.getProperties().getProperty(UserSettings.THEME.toString());

        var textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMouseTransparent(true);
        textArea.setFocusTraversable(false);
        textArea.getStylesheets().add(App.class.getResource("styles/" + theme + ".css").toExternalForm());
        textArea.getStyleClass().add("popup");
        FXMLUtils.adjustTextAreaDimensions(textArea);

        return textArea;
    }

    private static void setUpNewPopup(Stage window, Popup popup, TextArea textArea) {
        popup.getContent().addAll(textArea);
        setUpListeners(window, popup);
        notificationPopup = popup;
    }

    private static void showNewPopup(Stage window, Popup popup) {
        var transitions = getTransitions(popup);
        popup.show(window);
        placePopupCorrectly(window, popup);
        transitions.forEach(transition -> transition.play());
    }

    private static void setUpListeners(Stage window, Popup popup) {
        var scene = window.getScene();

        // Store listeners so we can remove them later
        ChangeListener<Number> xListener = (obs, oldX, newX) -> placePopupCorrectly(window, popup);
        ChangeListener<Number> yListener = (obs, oldY, newY) -> placePopupCorrectly(window, popup);
        ChangeListener<Number> sceneWListener = (obs, oldW, newW) -> placePopupCorrectly(window, popup);
        ChangeListener<Number> sceneHListener = (obs, oldH, newH) -> placePopupCorrectly(window, popup);
        ChangeListener<Boolean> focusListener = (obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                popup.hide();
            }
        };

        // Add listeners for window position and focus changes
        window.xProperty().addListener(xListener);
        window.yProperty().addListener(yListener);
        window.focusedProperty().addListener(focusListener);
        // Add listeners for scene size changes
        scene.widthProperty().addListener(sceneWListener);
        scene.heightProperty().addListener(sceneHListener);

        // Remove all listeners when the popup is hidden
        popup.setOnHidden(e -> {
            window.xProperty().removeListener(xListener);
            window.yProperty().removeListener(yListener);
            window.focusedProperty().removeListener(focusListener);
            scene.widthProperty().removeListener(sceneWListener);
            scene.heightProperty().removeListener(sceneHListener);
        });
    }

    private static void placePopupCorrectly(Stage parentWindow, Popup popup) {
        final int xOffset = 10; // Offset from the right edge
        final int yOffset = 10; // Offset from the bottom edge
        double windowX = parentWindow.getX();
        double windowY = parentWindow.getY();
        double sceneX = parentWindow.getScene().getX();
        double sceneY = parentWindow.getScene().getY();
        double sceneWidth = parentWindow.getScene().getWidth();
        double sceneHeight = parentWindow.getScene().getHeight();

        popup.setX(windowX + sceneX + sceneWidth - popup.getWidth() - xOffset);
        popup.setY(windowY + sceneY + sceneHeight - popup.getHeight() - yOffset);
    }

    private static ArrayList<Transition> getTransitions(Popup popup) {
        var transitions = new ArrayList<Transition>();
        transitions.add(getTranslateTransition(popup));
        transitions.add(getFadeTransition(popup));

        return transitions;
    }

    private static TranslateTransition getTranslateTransition(Popup popup) {
        TranslateTransition translateTransition = new TranslateTransition(
                Duration.seconds(0.15),
                popup.getContent().get(0));
        translateTransition.setFromY(popup.getHeight() + 30);
        translateTransition.setToY(0);

        return translateTransition;
    }

    private static FadeTransition getFadeTransition(Popup popup) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4.9), popup.getContent().get(0));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(event -> popup.hide());

        return fadeTransition;
    }

    // endregion
}
