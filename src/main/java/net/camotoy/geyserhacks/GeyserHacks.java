package net.camotoy.geyserhacks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class GeyserHacks extends JavaPlugin {

    @Override
    public void onEnable() {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(getDataFolder().toPath().resolve("geyserhacks.conf"))
                .defaultOptions(opts -> opts.header("Geyser "))
                .build();

        final GeyserHacksConfiguration config;
        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(GeyserHacksConfiguration.class);
            loader.save(node);
        } catch (ConfigurateException e) {
            getLogger().warning("Could not load config!");
            e.printStackTrace();
            return;
        }

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

        if (config.signFix()) {
            try {
                Block.class.getMethod("getState", boolean.class);
                Bukkit.getPluginManager().registerEvents(new SignUpdateFix(this), this);
            } catch (NoSuchMethodException e) {
                getLogger().warning("Cannot enable sign editing fix! Make sure you're running a decently new version of Paper.");
            }
        }
    }
}
