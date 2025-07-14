package com.celatus.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

  public static void moveFile(String sourcePath, String destPath) throws IOException {
    File sourceFile = new File(sourcePath);
    File destFile = new File(destPath);
    if (!sourceFile.exists()) {
      throw new IOException("Source file does not exist: " + sourcePath);
    }
    // Try rename first (works for most cases)
    boolean success = sourceFile.renameTo(destFile);
    if (!success) {
      // Fallback: copy and delete
      try (var in = new FileInputStream(sourceFile);
          var out = new FileOutputStream(destFile)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
          out.write(buffer, 0, length);
        }
      }

      sourceFile.delete();
    }
  }

  // endregion

}
