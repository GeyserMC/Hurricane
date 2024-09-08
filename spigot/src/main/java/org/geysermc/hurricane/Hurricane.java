package org.geysermc.hurricane;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.hurricane.config.ConfigLoader;
import org.geysermc.hurricane.config.HurricaneConfiguration;
import org.spongepowered.configurate.ConfigurateException;

public final class Hurricane extends JavaPlugin {

    @Override
    public void onEnable() {
        final HurricaneConfiguration config;
        try {
            config = ConfigLoader.loadConfig(getDataFolder().toPath());
        } catch (ConfigurateException e) {
            getLogger().warning("Could not load config!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
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
    }
}
