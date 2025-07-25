package com.celatus.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUtils {

  private static final Logger logger = LogManager.getLogger(App.class.getName());

  // region =====Json Serialization=====

  public static String mapToJson(@SuppressWarnings("rawtypes") Map map, boolean pretty) {
    String json = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    try {
      if (pretty) {
        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
      } else {
        json = objectMapper.writeValueAsString(map);
      }
    } catch (JsonProcessingException ex) {
      logger.error("An error occured while trying to convert map to json: " + ex);
    }
    return json;
  }

  @SuppressWarnings("unchecked")
  public static <T> Map<String, T> jsonToMap(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    return (Map<String, T>) jsonToObject(json, Map.class);
  }

  public static <T> String objectToJson(T obj, boolean pretty) {
    String json = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    try {
      if (pretty) {
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
    objectMapper.findAndRegisterModules();
    try {
      return objectMapper.readValue(jsonString, targetClass);
    } catch (JsonProcessingException ex) {
      logger.error("An error occurred while trying to convert JSON to object: " + ex);
      return null;
    }
  }

  // endregion

  // region =====List Maps=====
  // (Maps that follow the scheme : Map<Integer, T>, and are sorted in ascending
  // order)

  /**
   * Removes all of the entry sets of the given map where the key is higher
   * than the given index.
   */
  public static <T> void removeAllAfter(int index, Map<Integer, T> map) {
    // for (int key : map.keySet()) {
    // if (key <= index)
    // continue;
    // map.remove(key);
    // }
    Iterator<Integer> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      int key = iterator.next();
      if (key > index) {
        iterator.remove(); // Safe removal using iterator
      }
    }
  }

  /**
   * Removes the key value pair of the given map at the given key index,
   * and decrements the integer values of all higher indexes.
   * Similar to removing the first element of a queue.
   */
  public static <T> void remove(int index, Map<Integer, T> map) {
    map.remove(index);
    for (int key : map.keySet()) {
      if (key > index) {
        key--;
      }
    }
  }

  // endregion
}
