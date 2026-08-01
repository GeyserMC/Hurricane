package org.geysermc.hurricane.modded;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HurricaneModded {
    public static final Logger LOGGER = LoggerFactory.getLogger("Hurricane");

    private static volatile BedrockPlayerResolver playerResolver;

    private HurricaneModded() {
    }

    public static void initialize() {
        try {
            Optional<BedrockPlayerResolver> detectedResolver =
                    BedrockPlayerResolver.detect();
            if (detectedResolver.isEmpty()) {
                playerResolver = null;
                LOGGER.error(
                        "Hurricane requires a compatible Floodgate or Geyser mod; "
                                + "the collision adapter is disabled."
                );
                return;
            }
            playerResolver = detectedResolver.get();
            LOGGER.info("Per-player Bedrock collision fixes enabled.");
        } catch (LinkageError | RuntimeException error) {
            playerResolver = null;
            LOGGER.error(
                    "Hurricane requires a compatible Floodgate or Geyser API; "
                            + "the collision adapter is disabled.",
                    error
            );
        }
    }

    public static boolean isBedrockPlayer(UUID uuid) {
        BedrockPlayerResolver current = playerResolver;
        if (current == null) {
            return false;
        }
        try {
            return current.isBedrockPlayer(uuid);
        } catch (LinkageError | RuntimeException error) {
            return false;
        }
    }

}
