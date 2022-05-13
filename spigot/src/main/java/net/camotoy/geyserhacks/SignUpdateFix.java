package net.camotoy.geyserhacks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.GeyserImpl;

import java.util.UUID;
import java.util.function.Predicate;

public final class SignUpdateFix implements Listener {
    private final Predicate<UUID> playerChecker;
    private final Plugin plugin;

    public SignUpdateFix(Plugin plugin) {
        Predicate<UUID> playerChecker;
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            playerChecker = uuid -> FloodgateApi.getInstance().isFloodgatePlayer(uuid);
            plugin.getLogger().info("Sign update enabled; using Floodgate to check players.");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.geysermc.geyser.GeyserImpl");
                playerChecker = uuid -> GeyserImpl.getInstance().connectionByUuid(uuid) != null;
                plugin.getLogger().info("Sign update enabled; using Geyser to check players.");
            } catch (ClassNotFoundException e2) {
                plugin.getLogger().warning("Could not find Geyser or Floodgate; all players will be subject to sign update workaround!");
                playerChecker = uuid -> true;
            }
        }
        this.playerChecker = playerChecker;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Highest priority so we don't do meaningless updates if the event is cancelled
    public void onSignUpdate(final SignChangeEvent event) {
        if (playerChecker.test(event.getPlayer().getUniqueId())) {
            final Block block = event.getBlock();
            // Run on the next tick so the event is called and the result of this event is applied
            Bukkit.getScheduler().runTask(plugin, () -> {
                final BlockState state = block.getState(false); // Don't use snapshot as true doesn't apply the change
                if (!(state instanceof Sign)) {
                    plugin.getLogger().warning("Block was not sign? " + block.getLocation());
                    return;
                }
                ((Sign) state).setEditable(true);
            });
        }
    }
}
