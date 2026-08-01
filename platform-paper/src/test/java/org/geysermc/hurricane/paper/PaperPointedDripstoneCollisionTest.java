package org.geysermc.hurricane.paper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PointedDripstone;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.junit.jupiter.api.Test;

class PaperPointedDripstoneCollisionTest {
    @Test
    void mapsDownwardPointedDripstoneToHangingBedrockShape() {
        BlockPosition position = new BlockPosition(-8, 80, -8);

        Aabb upward = PaperPointedDripstoneCollision.boxAt(
                position,
                PointedDripstone.Thickness.TIP,
                BlockFace.UP
        );
        Aabb downward = PaperPointedDripstoneCollision.boxAt(
                position,
                PointedDripstone.Thickness.TIP,
                BlockFace.DOWN
        );

        assertEquals(80.0D, upward.minY());
        assertEquals(80.0D + 11.0D / 16.0D, upward.maxY());
        assertEquals(80.0D + 5.0D / 16.0D, downward.minY());
        assertEquals(81.0D, downward.maxY());
    }
}
