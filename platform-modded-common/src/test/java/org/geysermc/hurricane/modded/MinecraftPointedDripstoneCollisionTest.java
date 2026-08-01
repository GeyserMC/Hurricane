package org.geysermc.hurricane.modded;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.junit.jupiter.api.Test;

class MinecraftPointedDripstoneCollisionTest {
    @Test
    void mapsDownwardPointedDripstoneToHangingBedrockShape() {
        BlockPosition position = new BlockPosition(7, 80, 11);

        Aabb upward = MinecraftPointedDripstoneCollision.boxAt(
                position,
                SpeleothemThickness.TIP,
                Direction.UP
        );
        Aabb downward = MinecraftPointedDripstoneCollision.boxAt(
                position,
                SpeleothemThickness.TIP,
                Direction.DOWN
        );

        assertEquals(80.0D, upward.minY());
        assertEquals(80.0D + 11.0D / 16.0D, upward.maxY());
        assertEquals(80.0D + 5.0D / 16.0D, downward.minY());
        assertEquals(81.0D, downward.maxY());
    }
}
