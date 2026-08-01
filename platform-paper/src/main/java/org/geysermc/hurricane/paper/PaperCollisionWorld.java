package org.geysermc.hurricane.paper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.BoundingBox;
import org.geysermc.hurricane.core.collision.BedrockBambooCollision;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.geysermc.hurricane.core.math.Movement;

// PointedDripstone was renamed to Speleothem in 26.2 (hence the removal warning); Speleothem is
// absent on the 1.20.5 minimum API, so this alias is the only name valid across every supported version.
@SuppressWarnings("removal")
public final class PaperCollisionWorld {
    private PaperCollisionWorld() {
    }

    public static TargetScan scanTarget(
            Player player,
            Aabb target,
            boolean bambooEnabled,
            boolean pointedDripstoneEnabled
    ) {
        World world = player.getWorld();
        boolean javaTranslatedOverlap = false;
        boolean bedrockTranslatedOverlap = false;
        boolean otherBlockCollision = false;

        for (Block block : blocksInside(world, target)) {
            Aabb bedrockBox = bedrockCollisionBox(
                    block,
                    bambooEnabled,
                    pointedDripstoneEnabled
            );
            if (bedrockBox != null) {
                javaTranslatedOverlap |= worldShapeOverlaps(
                        block.getCollisionShape().getBoundingBoxes(),
                        block.getX(),
                        block.getY(),
                        block.getZ(),
                        target
                );
                bedrockTranslatedOverlap |= bedrockBox.overlaps(target);
            } else if (block.isCollidable() && worldShapeOverlaps(
                    block.getCollisionShape().getBoundingBoxes(),
                    block.getX(),
                    block.getY(),
                    block.getZ(),
                    target
            )) {
                otherBlockCollision = true;
            }
        }

        boolean entityCollision = !world.getNearbyEntities(
                PaperBoxes.toPaper(target),
                entity -> collidesWithPlayer(entity, player)
        ).isEmpty();
        return new TargetScan(
                javaTranslatedOverlap,
                bedrockTranslatedOverlap,
                otherBlockCollision,
                entityCollision
        );
    }

    public static List<Aabb> bedrockCollisionBoxes(
            World world,
            Aabb sweptBounds,
            boolean bambooEnabled,
            boolean pointedDripstoneEnabled
    ) {
        List<Aabb> boxes = new ArrayList<>();
        for (Block block : blocksInside(world, sweptBounds)) {
            Aabb box = bedrockCollisionBox(block, bambooEnabled, pointedDripstoneEnabled);
            if (box != null) {
                boxes.add(box);
            }
        }
        return boxes;
    }

    private static Aabb bedrockCollisionBox(
            Block block,
            boolean bambooEnabled,
            boolean pointedDripstoneEnabled
    ) {
        BlockPosition position =
                new BlockPosition(block.getX(), block.getY(), block.getZ());
        if (bambooEnabled && block.getType() == Material.BAMBOO) {
            return BedrockBambooCollision.boxAt(position);
        }
        if (!pointedDripstoneEnabled || block.getType() != Material.POINTED_DRIPSTONE) {
            return null;
        }

        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof PointedDripstone pointedDripstone)) {
            return null;
        }
        return PaperPointedDripstoneCollision.boxAt(
                position,
                pointedDripstone.getThickness(),
                pointedDripstone.getVerticalDirection()
        );
    }

    static boolean worldShapeOverlaps(
            Collection<BoundingBox> localShape,
            int blockX,
            int blockY,
            int blockZ,
            Aabb target
    ) {
        Movement blockOffset = new Movement(blockX, blockY, blockZ);
        for (BoundingBox local : localShape) {
            if (PaperBoxes.toCore(local).move(blockOffset).overlaps(target)) {
                return true;
            }
        }
        return false;
    }

    private static List<Block> blocksInside(World world, Aabb bounds) {
        int minX = floor(bounds.minX());
        int minY = Math.max(world.getMinHeight(), floor(bounds.minY()));
        int minZ = floor(bounds.minZ());
        int maxX = floor(Math.nextDown(bounds.maxX()));
        int maxY = Math.min(world.getMaxHeight() - 1, floor(Math.nextDown(bounds.maxY())));
        int maxZ = floor(Math.nextDown(bounds.maxZ()));
        List<Block> blocks = new ArrayList<>(
                Math.max(0, maxX - minX + 1)
                        * Math.max(0, maxY - minY + 1)
                        * Math.max(0, maxZ - minZ + 1)
        );

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                    continue;
                }
                for (int y = minY; y <= maxY; y++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    private static boolean collidesWithPlayer(Entity entity, Player player) {
        if (entity == player) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            boolean exempt = livingEntity.getCollidableExemptions().contains(player.getUniqueId());
            return livingEntity.isCollidable() != exempt;
        }
        return entity instanceof Vehicle || entity instanceof EnderCrystal;
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }

    public record TargetScan(
            boolean javaTranslatedOverlap,
            boolean bedrockTranslatedOverlap,
            boolean otherBlockCollision,
            boolean entityCollision
    ) {
    }
}
