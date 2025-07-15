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
import javafx.scene.layout.Pane;
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
        Pane rowPaneForPlacement = (Pane) window.getScene().lookup("#rowPane1");
        if (rowPaneForPlacement == null)
            return;

        hideActiveNotificationPopup();

        Popup popup = new Popup();
        var textArea = buildTextArea(message);

        setUpNewPopup(window, popup, textArea);
        showNewPopup(window, rowPaneForPlacement, popup);
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
        setUpListeners(window, popup, textArea);
        placePopupCorrectly(window, popup, textArea);
        notificationPopup = popup;
    }

    private static void showNewPopup(Stage window, Pane rowPaneForPlacement, Popup popup) {
        var transitions = getTransitions(popup, rowPaneForPlacement);
        popup.show(window);
        transitions.forEach(transition -> transition.play());
    }

    private static void setUpListeners(Stage window, Popup popup, TextArea textArea) {
        // Store listeners so we can remove them later
        ChangeListener<Number> xListener = (obs, oldX, newX) -> placePopupCorrectly(window, popup, textArea);
        ChangeListener<Number> yListener = (obs, oldY, newY) -> placePopupCorrectly(window, popup, textArea);
        ChangeListener<Number> wListener = (obs, oldW, newW) -> placePopupCorrectly(window, popup, textArea);
        ChangeListener<Number> hListener = (obs, oldH, newH) -> placePopupCorrectly(window, popup, textArea);
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

    private static void placePopupCorrectly(Stage parentWindow, Popup popup, TextArea popupText) {
        // The popup is located at the window's top middle
        popup.setX(parentWindow.getX()
                + (parentWindow.getWidth() / 2)
                - (FXMLUtils.computeTextWidth(popupText.getText(), popupText.getFont()) + 2) / 2);
        popup.setY(parentWindow.getY());
    }

    private static ArrayList<Transition> getTransitions(Popup popup, Pane rowPaneForPlacement) {
        var transitions = new ArrayList<Transition>();
        transitions.add(getTranslateTransition(rowPaneForPlacement, popup));
        transitions.add(getFadeTransition(popup));

        return transitions;
    }

    private static TranslateTransition getTranslateTransition(Pane rowPaneForPlacement, Popup popup) {
        TranslateTransition translateTransition = new TranslateTransition(
                Duration.seconds(0.1),
                popup.getContent().get(0));
        // Converting it to int otherwise the text is blurry
        translateTransition.setByY((int) (rowPaneForPlacement.getHeight() * 1.2));

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
