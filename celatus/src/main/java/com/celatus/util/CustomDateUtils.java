package com.celatus.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateUtils {

  public static String prettyDate(LocalDateTime dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return dateTime.format(formatter);
  }
}
