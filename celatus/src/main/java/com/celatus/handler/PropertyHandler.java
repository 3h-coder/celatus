package com.celatus.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.celatus.App;

public class PropertyHandler {

    // region =====Variables=====

    static final String PROPERTIES_FILE_PATH = "app.properties";

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

    // endregion
    
}
