package org.geysermc.hurricane.paper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class BedrockPlayerResolverTest {
    private static final UUID BEDROCK =
            UUID.fromString("6445f9b6-d138-4e46-9c36-67e12ee58cc6");
    private static final UUID JAVA =
            UUID.fromString("63b5b58d-606d-49f0-a872-687498ca2ef0");

    @Test
    void usesFloodgateWhenPresent() {
        BedrockPlayerResolver resolver = resolver(
                Optional.of(BEDROCK::equals),
                Optional.of(uuid -> false)
        );

        assertTrue(resolver.isBedrockPlayer(BEDROCK));
        assertFalse(resolver.isBedrockPlayer(JAVA));
    }

    @Test
    void doesNotFallBackToGeyserWhenFloodgateIsPresent() {
        AtomicBoolean geyserCalled = new AtomicBoolean();
        BedrockPlayerResolver resolver = resolver(
                Optional.of(uuid -> false),
                Optional.of(uuid -> {
                    geyserCalled.set(true);
                    return true;
                })
        );

        assertFalse(resolver.isBedrockPlayer(BEDROCK));
        assertFalse(geyserCalled.get());
    }

    @Test
    void usesGeyserWhenFloodgateIsAbsent() {
        BedrockPlayerResolver resolver = resolver(
                Optional.empty(),
                Optional.of(BEDROCK::equals)
        );

        assertTrue(resolver.isBedrockPlayer(BEDROCK));
        assertFalse(resolver.isBedrockPlayer(JAVA));
    }

    @Test
    void rejectsPlayersWhenBothProvidersAreAbsent() {
        BedrockPlayerResolver resolver = resolver(Optional.empty(), Optional.empty());

        assertFalse(resolver.isBedrockPlayer(BEDROCK));
        assertFalse(resolver.isBedrockPlayer(JAVA));
    }

    private static BedrockPlayerResolver resolver(
            Optional<Predicate<UUID>> floodgate,
            Optional<Predicate<UUID>> geyser
    ) {
        return new BedrockPlayerResolver(floodgate, geyser);
    }
}
