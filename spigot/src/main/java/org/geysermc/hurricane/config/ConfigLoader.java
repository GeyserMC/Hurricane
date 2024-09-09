package org.geysermc.hurricane.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import java.nio.file.Path;

public final class ConfigLoader {

    private final static ConfigurationTransformation.Versioned TRANSFORMER = ConfigurationTransformation.versionedBuilder()
            .addVersion(1, noneToOne())
            .build();

    private static final int LATEST_CONFIG_VERSION = 1;

    public static HurricaneConfiguration loadConfig(Path dataFolder) throws ConfigurateException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(dataFolder.resolve("hurricane.conf"))
                .defaultOptions(opts -> opts.header("Hurricane Configuration"))
                .build();

        final CommentedConfigurationNode node = loader.load();

        int version = TRANSFORMER.version(node);

        if (version != LATEST_CONFIG_VERSION) {
            TRANSFORMER.apply(node);
        }

        final HurricaneConfiguration config = node.get(HurricaneConfiguration.class);
        node.set(HurricaneConfiguration.class, config);

        // Save config again to e.g. add need options, or create the initial config
        // Hocon automatically sorts configuration options alphabetically, so we don't need an intermediary node
        loader.save(node);

        return config;
    }

    private static ConfigurationTransformation noneToOne() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("item-steerable-fix"), TransformAction.remove())
                .build();
    }
}
