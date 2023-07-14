package com.celatus;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUtils {

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    public static String mapToJson(Map map, boolean pretty) {
        String result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(pretty) {
                result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            } else {
                result = objectMapper.writeValueAsString(map);
            }
        } catch (JsonProcessingException ex) {
           logger.error("An error occured while trying to convert map to json: " + ex);
        }
        return result;
    }
    
}
