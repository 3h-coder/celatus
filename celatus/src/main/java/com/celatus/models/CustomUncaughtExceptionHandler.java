package com.celatus.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.App;
import com.celatus.enums.AlertMode;

/** Our custom implementation of the UncaughtExceptionHandler object */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  private static final Logger logger = LogManager.getLogger(App.class.getName());

  @Override
  public void uncaughtException(Thread t, Throwable ex) {

    App.error(App.getWindow(), ex, "An unexpected error occured", logger, AlertMode.OK, true);
    // _logger.error("Uncaught exception in thread " + t.getName() + ": " +
    // e.getMessage(), e);

    // You can perform additional actions here, like closing resources, saving data,
    // etc.

    // Exit the program (you can choose the appropriate exit code)
    System.exit(1);
  }
}
