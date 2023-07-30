package com.celatus.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUtils {

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    public static String mapToJson(Map map, boolean pretty) {
        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(pretty) {
                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            } else {
                json = objectMapper.writeValueAsString(map);
            }
        } catch (JsonProcessingException ex) {
           logger.error("An error occured while trying to convert map to json: " + ex);
        }
        return json;
    }

    public static <T> String objectToJson(T obj, boolean pretty) {
        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();
         try {
            if(pretty) {
                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                json = objectMapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException ex) {
           logger.error("An error occured while trying to convert map to json: " + ex);
        }
        return json;
    }

    public static <T> T jsonToObject(String jsonString, Class<T> targetClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (JsonProcessingException ex) {
            logger.error("An error occurred while trying to convert JSON to object: " + ex);
            return null;
        }     
    }
    
}
