package org.geysermc.hurricane.paper;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HurricanePaper extends JavaPlugin {
    private static final String FAIL_MOVE_EVENT =
            "io.papermc.paper.event.player.PlayerFailMoveEvent";

    @Override
    public void onEnable() {
        if (!isCompatiblePaper()) {
            getLogger().severe(
                    "Hurricane requires Paper 1.20.5 or newer with PlayerFailMoveEvent; "
                            + "Spigot is not supported."
            );
            disable();
            return;
        }

        PluginManager plugins = Bukkit.getPluginManager();
        saveDefaultConfig();
        boolean bambooEnabled = getConfig().getBoolean("collision-fixes.bamboo", true);
        boolean pointedDripstoneEnabled =
                getConfig().getBoolean("collision-fixes.pointed-dripstone", true);
        if (!bambooEnabled && !pointedDripstoneEnabled) {
            getLogger().info("All Bedrock collision fixes are disabled.");
            return;
        }

        Optional<BedrockPlayerResolver> detectedResolver;
        try {
            detectedResolver = BedrockPlayerResolver.detect(getLogger());
        } catch (LinkageError | RuntimeException error) {
            getLogger().severe(
                    "The installed Floodgate or Geyser API is unavailable or incompatible."
            );
            disable();
            return;
        }
        if (detectedResolver.isEmpty()) {
            getLogger().severe(
                    "Hurricane requires Floodgate or Geyser-Spigot on this server."
            );
            disable();
            return;
        }
        BedrockPlayerResolver resolver = detectedResolver.get();

        plugins.registerEvents(
                new PaperCollisionListener(resolver, bambooEnabled, pointedDripstoneEnabled),
                this
        );
        getLogger().info("Per-player Bedrock collision fixes enabled.");
    }

    private boolean isCompatiblePaper() {
        try {
            Class.forName(FAIL_MOVE_EVENT, false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    private void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
