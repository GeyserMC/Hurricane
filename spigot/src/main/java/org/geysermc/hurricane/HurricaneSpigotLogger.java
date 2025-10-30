package org.geysermc.hurricane;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class HurricaneSpigotLogger implements HurricaneLogger {

    private final Logger logger;

    public HurricaneSpigotLogger(@NotNull Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(String.format(message, args));
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warning(String.format(message, args));
    }
}
