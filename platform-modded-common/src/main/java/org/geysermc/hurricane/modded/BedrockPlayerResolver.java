package org.geysermc.hurricane.modded;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

public final class BedrockPlayerResolver {
    private static final String FLOODGATE_API =
            "org.geysermc.floodgate.api.FloodgateApi";
    private static final String GEYSER_API =
            "org.geysermc.geyser.api.GeyserApi";

    private final Optional<Predicate<UUID>> floodgate;
    private final Optional<Predicate<UUID>> geyser;

    BedrockPlayerResolver(
            Optional<Predicate<UUID>> floodgate,
            Optional<Predicate<UUID>> geyser
    ) {
        this.floodgate = Objects.requireNonNull(floodgate, "floodgate");
        this.geyser = Objects.requireNonNull(geyser, "geyser");
    }

    public static Optional<BedrockPlayerResolver> detect() {
        if (isAvailable(FLOODGATE_API)) {
            return Optional.of(new BedrockPlayerResolver(
                    Optional.of(FloodgateProvider::isBedrockPlayer),
                    Optional.empty()
            ));
        }
        if (isAvailable(GEYSER_API)) {
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

    private static boolean isAvailable(String className) {
        try {
            Class.forName(className, false, BedrockPlayerResolver.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
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
