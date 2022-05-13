package net.camotoy.geyserhacks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Predicate;

public final class ItemSteerableFix implements Listener {
    private final Predicate<UUID> playerChecker;
    private final Plugin plugin;
    private final NMSProvider provider;
    /**
     * The value here should be whatever Vec3 is
     */
    private final Map<Player, Object> players = new Object2ObjectOpenHashMap<>(0);
    private int taskId = -1;

    public ItemSteerableFix(final Plugin plugin, final Predicate<UUID> playerChecker, final NMSProvider provider) {
        this.playerChecker = playerChecker;
        this.plugin = plugin;
        this.provider = provider;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEnter(final VehicleEnterEvent event) {
        final Entity entering = event.getEntered();
        if (entering instanceof Player && event.getVehicle() instanceof Steerable && playerChecker.test(entering.getUniqueId())) {
            players.put((Player) entering, provider.getVec3Zero());
            if (taskId == -1) {
                taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::onTick, 1, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onExit(final VehicleExitEvent event) {
        final LivingEntity exited = event.getExited();
        if (exited instanceof Player) {
            players.remove(exited);

            if (players.isEmpty() && taskId != -1) {
                Bukkit.getScheduler().cancelTask(taskId);
                taskId = -1;
            }
        }
    }

    private void onTick() {
        final Iterator<Map.Entry<Player, Object>> it = players.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Player, Object> entry = it.next();
            final Entity entity = entry.getKey().getVehicle();
            if (!(entity instanceof Steerable)) {
                it.remove();
                continue;
            }
            provider.forEachPlayerOnSteerable(entity, entry);
        }
    }
}
