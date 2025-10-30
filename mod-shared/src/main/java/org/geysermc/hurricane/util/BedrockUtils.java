package org.geysermc.hurricane.util;

import org.geysermc.hurricane.Hurricane;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class BedrockUtils {

    private static boolean floodgatePresent;

    private static boolean geyserPresent = false;

    static {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgatePresent = true;
        } catch (ClassNotFoundException e) {
            floodgatePresent = false;
            try {
                Class.forName("org.geysermc.geyser.api.GeyserApi");
                geyserPresent = true;
            } catch (ClassNotFoundException ex) {
                geyserPresent = false;
                Hurricane.LOGGER.debug("Geyser/Floodgate not found, disabling Bedrock support.");
            }
        }
    }

    public static boolean isGeyserOrFloodgateInstalled() {
            return floodgatePresent || geyserPresent;
    }

    public static boolean isBedrockPlayer(UUID uuid){
        if (floodgatePresent) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }

        if (geyserPresent) {
            return GeyserApi.api().isBedrockPlayer(uuid);
        }

        return false;
    }
}