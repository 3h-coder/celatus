package com.celatus.enums;

/**
 * Temp variables are objects stored in memory at a global app level.
 * 
 * As their name suggests, they are meant to be temporary and therefore
 * retrieved and destroyed
 * as soon as possible.
 * Typically used for workflows in between windows, especially when one of them
 * closes.
 * 
 * Variables starting with the SIGNAL_ prefix should be of boolean type.
 */
public enum AppTempVariable {
    PASSWORD_RECORD,
    SIGNAL_MASTER_PASSWORD_RESET,
    SIGNAL_YES,
    SIGNAL_NO,
    SIGNAL_OK
}
