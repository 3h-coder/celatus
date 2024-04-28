package com.celatus.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

public class DesktopUtils {

  // region =====Public Methods=====

  public static boolean isCurrentDirWritable() {
    return isDirectoryWritable(System.getProperty("user.dir"));
  }

  public static boolean isDirectoryWritable(String path) {
    File file = new File(path, "cel_tmp.txt");
    boolean canWrite = false;

    try {
      canWrite = file.createNewFile();
    } catch (IOException ex) {
      // Do nothing
    } finally {
      if (file.exists()) {
        file.delete();
      }
    }

    return canWrite;
  }

  public static void copyToClipBoard(String string) {
    StringSelection stringSelection = new StringSelection(string);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }

  // endregion

}
