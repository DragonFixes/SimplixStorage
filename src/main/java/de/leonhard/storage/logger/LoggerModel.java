package de.leonhard.storage.logger;

public interface LoggerModel {
    void printMessage(Object object);
    void printMessage(String message);
    void printStackTrace(Exception exception);
}
