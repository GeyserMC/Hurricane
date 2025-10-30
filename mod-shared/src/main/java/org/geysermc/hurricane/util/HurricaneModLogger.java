package org.geysermc.hurricane.util;

import org.geysermc.hurricane.HurricaneLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HurricaneModLogger implements HurricaneLogger {

    private final Logger logger = LoggerFactory.getLogger("Hurricane");

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }
}
