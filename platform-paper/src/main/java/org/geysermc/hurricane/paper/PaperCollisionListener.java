package org.geysermc.hurricane.paper;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.geysermc.hurricane.core.collision.MovementResolver;
import org.geysermc.hurricane.core.collision.ResolvedMovement;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.Movement;

public final class PaperCollisionListener implements Listener {
    private static final double TARGET_INSET = 0.00001D;
    private static final double MAX_PACKET_MOVE = 8.0D;

    private final BedrockPlayerResolver playerResolver;
    private final boolean bambooEnabled;
    private final boolean pointedDripstoneEnabled;

    public PaperCollisionListener(
            BedrockPlayerResolver playerResolver,
            boolean bambooEnabled,
            boolean pointedDripstoneEnabled
    ) {
        this.playerResolver = Objects.requireNonNull(playerResolver, "playerResolver");
        this.bambooEnabled = bambooEnabled;
        this.pointedDripstoneEnabled = pointedDripstoneEnabled;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFailMove(PlayerFailMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFailReason() != PlayerFailMoveEvent.FailReason.CLIPPED_INTO_BLOCK
                || !playerResolver.isBedrockPlayer(player.getUniqueId())
                || !sameWorld(event.getFrom(), event.getTo())) {
            return;
        }

        Aabb target = PaperBoxes.deflate(boxAt(player, event.getTo()), TARGET_INSET);
        PaperCollisionWorld.TargetScan scan = PaperCollisionWorld.scanTarget(
                player,
                target,
                bambooEnabled,
                pointedDripstoneEnabled
        );
        PaperMovementDecision.FailedMoveEvaluation evaluation =
                new PaperMovementDecision.FailedMoveEvaluation(
                        true,
                        true,
                        scan.javaTranslatedOverlap(),
                        scan.bedrockTranslatedOverlap(),
                        scan.otherBlockCollision(),
                        scan.entityCollision()
                );
        if (PaperMovementDecision.shouldAllowFailedMove(evaluation)) {
            event.setAllowed(true);
            event.setLogWarning(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent
                || !event.hasChangedPosition()
                || !playerResolver.isBedrockPlayer(event.getPlayer().getUniqueId())
                || !sameWorld(event.getFrom(), event.getTo())) {
            return;
        }

        Movement requested = new Movement(
                event.getTo().getX() - event.getFrom().getX(),
                event.getTo().getY() - event.getFrom().getY(),
                event.getTo().getZ() - event.getFrom().getZ()
        );
        if (isImplausiblyLarge(requested)) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        Aabb start = boxAt(player, event.getFrom());
        Aabb target = boxAt(player, event.getTo());
        List<Aabb> collisionBoxes = PaperCollisionWorld.bedrockCollisionBoxes(
                player.getWorld(),
                start.union(target),
                bambooEnabled,
                pointedDripstoneEnabled
        );
        ResolvedMovement resolved = MovementResolver.resolve(start, target, collisionBoxes);
        if (!resolved.collided()) {
            return;
        }

        Movement movement = resolved.movement();
        Location corrected = event.getTo().clone();
        corrected.setX(event.getFrom().getX() + movement.x());
        corrected.setY(event.getFrom().getY() + movement.y());
        corrected.setZ(event.getFrom().getZ() + movement.z());
        event.setTo(corrected);
    }

    private static Aabb boxAt(Player player, Location location) {
        Location current = player.getLocation();
        return PaperBoxes.atPosition(
                player.getBoundingBox(),
                current.getX(),
                current.getY(),
                current.getZ(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    private static boolean sameWorld(Location from, Location to) {
        World fromWorld = from.getWorld();
        return fromWorld != null && fromWorld.equals(to.getWorld());
    }

    private static boolean isImplausiblyLarge(Movement movement) {
        return Math.abs(movement.x()) > MAX_PACKET_MOVE
                || Math.abs(movement.y()) > MAX_PACKET_MOVE
                || Math.abs(movement.z()) > MAX_PACKET_MOVE;
    }
}
