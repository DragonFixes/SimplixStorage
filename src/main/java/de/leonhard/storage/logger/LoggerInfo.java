package de.leonhard.storage.logger;

import lombok.Getter;
import lombok.Setter;

public class LoggerInfo {

    @Setter @Getter
    private static LoggerModel logger = new PrinterLogger();
}
