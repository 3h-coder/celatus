package com.celatus.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;

public class PropertyHandler {

  // region =====Variables=====

  private static final Logger logger = LogManager.getLogger(PropertyHandler.class.getName());

  private static final String PROPERTIES_FILE_PATH = "app.properties";

  private static HashMap<String, List<String>> propertyMap;

  // endregion

  // region =====Static Init=====

  static {
    propertyMap = new HashMap<>();
    // The first elemnt of the list is the default
    propertyMap.put("theme", List.of("default", "light"));
    propertyMap.put("show_password", List.of("false", "true"));
  }

  // endregion

  // region =====Methods=====

  public static boolean propertyFileExists() {
    File file = new File(PROPERTIES_FILE_PATH);
    return file.exists();
  }

  public static Properties readProperties() {
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
      properties.load(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return properties;
  }

  public static void writeProperties(Properties properties) {
    try (OutputStream output = new FileOutputStream(PROPERTIES_FILE_PATH)) {
      properties.store(output, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void createDefaultProperties() {
    Properties properties = new Properties();
    properties.setProperty("theme", "default");
    writeProperties(properties);
  }

  /**
   * Deletes unknown properties and resets unkown values to default, before saving them.
   *
   * @param properties
   */
  public static void checkProperties(Properties properties) {

    for (var entry : properties.entrySet()) {
      String propertyName = (String) entry.getKey();
      String propertyValue = (String) entry.getValue();

      // Removing unkown properties
      if (!propertyMap.containsKey(propertyName)) {
        logger.debug("Unknown property : " + propertyName + " -> removing it.");
        properties.remove(propertyName);
        continue;
      }
      // Resetting unknown values
      if (!propertyMap.get(propertyName).contains(propertyValue)) {
        logger.debug(
            "The following value is not recognized for the "
                + propertyName
                + " property: "
                + propertyValue);
        String defaultProperty = propertyMap.get(propertyName).get(0);
        logger.debug("Setting " + propertyName + " to " + defaultProperty);
        properties.setProperty(propertyName, defaultProperty);
      }
    }
    // Saving the properties
    writeProperties(properties);
  }

  // endregion

}
