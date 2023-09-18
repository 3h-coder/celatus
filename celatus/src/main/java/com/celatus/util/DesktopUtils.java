package com.celatus.util;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

public class DesktopUtils {

  public static void copyToClipBoard(String string) {
    StringSelection stringSelection = new StringSelection(string);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }
}
