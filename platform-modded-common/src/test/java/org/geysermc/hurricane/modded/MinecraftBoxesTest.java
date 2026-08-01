package org.geysermc.hurricane.modded;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.geysermc.hurricane.core.collision.BedrockBambooCollision;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.junit.jupiter.api.Test;

class MinecraftBoxesTest {
    private static final double TOLERANCE = 0.0000001D;

    @Test
    void convertsWorldBoxToExactBlockLocalVoxelShape() {
        BlockPosition position = new BlockPosition(410, 161, 408);
        Aabb worldBox = BedrockBambooCollision.boxAt(position);

        VoxelShape shape = MinecraftBoxes.toLocalShape(worldBox, position);

        assertEquals(worldBox.minX() - position.x(), shape.min(Direction.Axis.X), TOLERANCE);
        assertEquals(worldBox.minY() - position.y(), shape.min(Direction.Axis.Y), TOLERANCE);
        assertEquals(worldBox.minZ() - position.z(), shape.min(Direction.Axis.Z), TOLERANCE);
        assertEquals(worldBox.maxX() - position.x(), shape.max(Direction.Axis.X), TOLERANCE);
        assertEquals(worldBox.maxY() - position.y(), shape.max(Direction.Axis.Y), TOLERANCE);
        assertEquals(worldBox.maxZ() - position.z(), shape.max(Direction.Axis.Z), TOLERANCE);
    }
}
