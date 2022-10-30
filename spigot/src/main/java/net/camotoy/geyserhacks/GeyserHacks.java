package net.camotoy.geyserhacks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.GeyserImpl;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.function.Predicate;

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

        if (config.itemSteerableFix()) {
            NMSProvider providerImpl = null;
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String nmsVersion = name.substring(name.lastIndexOf('.') + 1);
            try {
                Class<?> providerImplClass = Class.forName("net.camotoy.geyserhacks." + nmsVersion + ".NMSProviderImpl");
                providerImpl = (NMSProvider) providerImplClass.getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                getLogger().warning("This Minecraft server version does not support the item steerable workaround!");
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (providerImpl != null) {
                Predicate<UUID> playerChecker;
                try {
                    Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                    playerChecker = uuid -> FloodgateApi.getInstance().isFloodgatePlayer(uuid);
                } catch (ClassNotFoundException e) {
                    try {
                        Class.forName("org.geysermc.geyser.GeyserImpl");
                        playerChecker = uuid -> GeyserImpl.getInstance().connectionByUuid(uuid) != null;
                    } catch (ClassNotFoundException e2) {
                        getLogger().warning("Could not find Geyser or Floodgate; item steerable fix will not be applied.");
                        playerChecker = null;
                    }
                }
                if (playerChecker != null) {
                    Bukkit.getPluginManager().registerEvents(new ItemSteerableFix(this, playerChecker, providerImpl), this);
                    getLogger().info("Item steerable fix enabled.");
                }
            }
        }
    }
}
