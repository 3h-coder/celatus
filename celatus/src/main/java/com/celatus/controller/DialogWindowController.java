package com.celatus.controller;

import com.celatus.util.FXMLUtils;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/** Base controller for all of our dialog window coontrollers */
public class DialogWindowController extends BaseWindowController {

  // region =====Variables=====

  public Stage owner;

  // endregion

  // region ======Window Methods=====

  @Override
  protected void lateInitialize() {
    super.lateInitialize();
    owner = (Stage) window.getOwner();
    addDarkOverlayOnOwner();
  }

  private void addDarkOverlayOnOwner() {
    if (owner != null) {
      FXMLUtils.addDarkOverlay(owner.getScene());
    }
  }

  private void removeDarkOverlayFromOwner() {
    if (owner != null) {
      FXMLUtils.removeDarkOverlay(owner.getScene());
    }
  }

  // endregion

  // region =====Event Methods=====

  @Override
  @FXML
  protected void windowKeyPressed(KeyEvent event) {
    super.windowKeyPressed(event);
    KeyCode eventCode = event.getCode();
    if (eventCode == KeyCode.ESCAPE) {
      closeDialog();
    }
  }

  @FXML
  public void closeDialog() {
    removeDarkOverlayFromOwner();
    window.close();
  }

  // endregion
}
