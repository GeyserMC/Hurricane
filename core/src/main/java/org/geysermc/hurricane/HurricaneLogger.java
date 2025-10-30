package org.geysermc.hurricane;

public interface HurricaneLogger {

    void info(String message);

    void info(String message, Object... args);

    void warn(String message);

    void warn(String message, Object... args);
}
