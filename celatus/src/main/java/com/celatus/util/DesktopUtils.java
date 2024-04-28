package com.celatus.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import com.celatus.App;

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

  public static String getLogFilePath() {
    return getUserAppPath() + "celatus.log";
  }

  // endregion

  // region =====Private Methods=====

  public static String getUserAppPath() {
    String userHome = SystemUtils.USER_HOME;
    String appDataPath = "";

    if (SystemUtils.IS_OS_WINDOWS) {
      appDataPath = System.getenv("LOCALAPPDATA") + File.separator + App.NAME;
    } else if (SystemUtils.IS_OS_MAC) {
      appDataPath = userHome + File.separator + "Library" + File.separator + "Application Support" + File.separator
          + App.NAME;
    } else {
      appDataPath = userHome + File.separator + "." + App.NAME.toLowerCase();
    }

    return appDataPath;
  }

  // endregion

}
