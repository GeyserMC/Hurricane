package org.geysermc.hurricane;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

public final class Hurricane extends JavaPlugin {

    private final ConfigurationTransformation.Versioned transformer = ConfigurationTransformation.versionedBuilder()
            .addVersion(1, noneToOne())
            .build();

    private static final int LATEST_CONFIG_VERSION = 1;

    @Override
    public void onEnable() {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(getDataFolder().toPath().resolve("hurricane.conf"))
                .defaultOptions(opts -> opts.header("Hurricane Configuration"))
                .build();

        final HurricaneConfiguration config;
        try {
            final CommentedConfigurationNode node = loader.load();

            int version = transformer.version(node);

            if (version != LATEST_CONFIG_VERSION) {
                transformer.apply(node);
            }

            config = node.get(HurricaneConfiguration.class);
            node.set(HurricaneConfiguration.class, config);

            loader.save(node);
        } catch (ConfigurateException e) {
            getLogger().warning("Could not load config!");
            e.printStackTrace();
            return;
        }

        assert config != null;
        final boolean bambooFixEnabled = config.collisionFixes().bamboo();

        final boolean pointedDripstoneFixEnabled;
        if (config.collisionFixes().pointedDripstone()) {
            if (NMSReflection.getMojmapNMSClass("world.level.block.PointedDripstoneBlock") != null) {
                pointedDripstoneFixEnabled = true;
            } else {
                getLogger().warning("Pointed dripstone collision fix enabled in settings but we're not in 1.17+.");
                pointedDripstoneFixEnabled = false;
            }
        } else {
            pointedDripstoneFixEnabled = false;
        }

        if (bambooFixEnabled || pointedDripstoneFixEnabled) {
            Bukkit.getPluginManager().registerEvents(new CollisionFix(this, bambooFixEnabled, pointedDripstoneFixEnabled), this);
        }
    }

    private ConfigurationTransformation noneToOne() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("item-steerable-fix"), TransformAction.remove())
                .build();
    }
}
