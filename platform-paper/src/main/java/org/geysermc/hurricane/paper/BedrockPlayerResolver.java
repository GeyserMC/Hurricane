package org.geysermc.hurricane.paper;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

public final class BedrockPlayerResolver {
    private final Optional<Predicate<UUID>> floodgate;
    private final Optional<Predicate<UUID>> geyser;

    BedrockPlayerResolver(
            Optional<Predicate<UUID>> floodgate,
            Optional<Predicate<UUID>> geyser
    ) {
        this.floodgate = Objects.requireNonNull(floodgate, "floodgate");
        this.geyser = Objects.requireNonNull(geyser, "geyser");
    }

    public static Optional<BedrockPlayerResolver> detect(Logger logger) {
        PluginManager plugins = Bukkit.getPluginManager();
        if (plugins.isPluginEnabled("floodgate")) {
            logger.info("Using Floodgate to identify Bedrock players.");
            return Optional.of(new BedrockPlayerResolver(
                    Optional.of(FloodgateProvider::isBedrockPlayer),
                    Optional.empty()
            ));
        }
        if (plugins.isPluginEnabled("Geyser-Spigot")) {
            logger.info("Using Geyser to identify Bedrock players.");
            return Optional.of(new BedrockPlayerResolver(
                    Optional.empty(),
                    Optional.of(GeyserProvider::isBedrockPlayer)
            ));
        }
        return Optional.empty();
    }

    public boolean isBedrockPlayer(UUID uuid) {
        if (floodgate.isPresent()) {
            return floodgate.get().test(uuid);
        }
        return geyser.map(provider -> provider.test(uuid)).orElse(false);
    }

    private static final class FloodgateProvider {
        private static boolean isBedrockPlayer(UUID uuid) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }
    }

    private static final class GeyserProvider {
        private static boolean isBedrockPlayer(UUID uuid) {
            return GeyserApi.api().isBedrockPlayer(uuid);
        }
    }
}
