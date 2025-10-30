package org.geysermc.hurricane;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.hurricane.config.ConfigLoader;
import org.geysermc.hurricane.config.HurricaneConfiguration;
import org.spongepowered.configurate.ConfigurateException;

public final class HurricaneSpigotPlatform extends JavaPlugin implements HurricanePlatform {

    private HurricaneConfiguration config;

    @Override
    public void onEnable() {
        try {
            config = ConfigLoader.loadConfig(getDataFolder().toPath());
        } catch (ConfigurateException e) {
            getLogger().warning("Could not load config!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        enable(config);
    }

    @Override
    public HurricaneConfiguration configuration() {
        return config;
    }

    @Override
    public HurricaneLogger logger() {
        return new HurricaneSpigotLogger(getLogger());
    }

    @Override
    public void registerCollisionFixes(HurricaneConfiguration configuration) {
        boolean bambooFixEnabled = configuration.collisionFixes().bamboo();
        boolean pointedDripstoneFixEnabled = configuration.collisionFixes().pointedDripstone();
        Bukkit.getPluginManager().registerEvents(new CollisionFix(this, bambooFixEnabled, pointedDripstoneFixEnabled), this);
    }

    @Override
    public void verifyConfig(HurricaneConfiguration configuration) {
        if (config.collisionFixes().pointedDripstone()) {
            if (NMSReflection.getMojmapNMSClass("world.level.block.PointedDripstoneBlock") == null) {
                logger().warn("Pointed dripstone collision fix enabled in settings but we're not in 1.17+.");
                configuration.collisionFixes().setPointedDripstone(false);
            }
        }
    }
}
