package org.geysermc.hurricane;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class BedrockUtils {

    private static boolean floodgatePresent;
    private static boolean geyserPresent;

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