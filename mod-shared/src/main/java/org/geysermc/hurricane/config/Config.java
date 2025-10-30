package org.geysermc.hurricane.config;

import org.geysermc.hurricane.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("Hurricane");
    private boolean bamboo;
    private boolean pointedDripstone;

    public static boolean shouldWarn = false;

    public boolean isBamboo() {
        return bamboo;
    }

    public boolean isPointedDripstone() {
        return pointedDripstone;
    }

    public static HurricaneConfiguration config;

    private final ConfigurationTransformation.Versioned transformer = ConfigurationTransformation.versionedBuilder()
            .addVersion(1, zeroToOne())
            .addVersion(2, oneToTwo())
            .build();

    public Config() {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(PlatformUtil.configPath())
                .defaultOptions(opts -> opts.header("Hurricane Configuration "))
                .prettyPrinting(true)
                .build();

        try {
            final CommentedConfigurationNode rootNode = loader.load();
            config = rootNode.get(HurricaneConfiguration.class);

            // Attempt to upgrade our config here if we don't have a version set
            var versionNode = rootNode.node("version");
            if (versionNode.virtual()) {
                shouldWarn = true;
                versionNode.set(0); // Add a version config entry, set it to 1
                versionNode.comment("The version of the config. DO NOT CHANGE!");

                // Remove item steerable workaround
                transformer.apply(rootNode);
            }

            rootNode.set(HurricaneConfiguration.class, config);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            LOGGER.warn("Could not load config!");
            e.printStackTrace();
            return;
        }

        this.bamboo = config.collisionFixes().bamboo();
        this.pointedDripstone = config.collisionFixes().pointedDripstone();
    }

    private ConfigurationTransformation zeroToOne() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("item-steerable-fix"), TransformAction.remove())
                .build();
    }

    private ConfigurationTransformation oneToTwo() {
        return ConfigurationTransformation.builder()
                // TODO double-check name; add new config option
                .addAction(NodePath.path("suppress-warnings"), TransformAction.remove())
                .build();
    }
}