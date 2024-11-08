package de.leonhard.storage.internal.settings;

public enum ErrorHandler {
    /**
     * Makes the file to return empty values, and keeps the old configuration.
     */
    EMPTY,
    /**
     * Keeps the last data saved to the file without errors.
     */
    KEEP,
    /**
     * Rollbacks the file to the last data save to the file without errors.
     */
    ROLLBACK,
    /** TODO later implementation
     * Set the default values if exists.
     */
    //SET_DEFAULT,
    /**
     * Clears the file.
     */
    CLEAR
}
