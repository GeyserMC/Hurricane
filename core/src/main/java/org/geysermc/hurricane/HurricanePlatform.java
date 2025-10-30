package org.geysermc.hurricane;

import org.geysermc.hurricane.config.HurricaneConfiguration;

public interface HurricanePlatform {

    HurricaneConfiguration configuration();

    HurricaneLogger logger();

    default void enable(HurricaneConfiguration configuration) {
        verifyConfig(configuration);

        if (configuration.collisionFixes().bamboo() || configuration.collisionFixes().pointedDripstone()) {
            registerCollisionFixes(configuration);
        }
    }

    void registerCollisionFixes(HurricaneConfiguration configuration);

    void verifyConfig(HurricaneConfiguration configuration);
}
