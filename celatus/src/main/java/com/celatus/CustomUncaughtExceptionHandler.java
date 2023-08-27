package com.celatus;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.celatus.controller.PopupMode;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LogManager.getLogger(App.class.getName());

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Log the uncaught exception
        App.error(App.getWindow(), "An unexpected error occured: " + e, logger, PopupMode.OK);

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.error("Full stack trace: " + sw.toString());
        //_logger.error("Uncaught exception in thread " + t.getName() + ": " + e.getMessage(), e);
        
        // You can perform additional actions here, like closing resources, saving data, etc.
        
        // Exit the program (you can choose the appropriate exit code)
        System.exit(1);
    }
}

