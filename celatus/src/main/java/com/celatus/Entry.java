package com.celatus;

/**
 * The jar generated from Maven fails run if the entry point
 * class inherits from another, so we have to create this
 * class as a the main entry point.
 */
public class Entry {
    public static void main(String[] args) {
        App.main(args);
    }
}
