package org.geysermc.hurricane.core.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.junit.jupiter.api.Test;

/**
 * The fixtures below are Bedrock values, not Java ones. They come from a Script API probe of
 * native pointed dripstone on Bedrock Dedicated Server 1.26.32.2, and describe the collision
 * box the Bedrock client renders, which is what this class reproduces.
 */
class BedrockPointedDripstoneCollisionTest {
    private static final double TOLERANCE = 0.000002D;
    private static final double FACE_INSET = (1.0D / 1024.0D) / 16.0D;
    private static final List<OffsetFixture> MEASURED_OFFSETS = List.of(
            new OffsetFixture(0, 0, -0.09166667F, -0.05833333F),
            new OffsetFixture(7, 11, 0.05833333F, -0.05833333F),
            new OffsetFixture(-8, -8, 0.05833333F, -0.00833333F),
            new OffsetFixture(15, -13, 0.00833333F, -0.09166667F)
    );

    @Test
    void reproducesBdsProbeOffsets() {
        for (OffsetFixture fixture : MEASURED_OFFSETS) {
            BedrockPointedDripstoneCollision.Offset actual =
                    BedrockPointedDripstoneCollision.offsetAt(
                            fixture.blockX(),
                            fixture.blockZ()
                    );

            assertEquals(fixture.offsetX(), actual.x(), TOLERANCE, fixture.toString());
            assertEquals(fixture.offsetZ(), actual.z(), TOLERANCE, fixture.toString());
        }
    }

    @Test
    void usesHalfTheBambooOffsetAtEveryPosition() {
        for (int x = -32; x <= 32; x++) {
            for (int z = -32; z <= 32; z++) {
                BedrockBambooCollision.Offset bamboo = BedrockBambooCollision.offsetAt(x, z);
                BedrockPointedDripstoneCollision.Offset dripstone =
                        BedrockPointedDripstoneCollision.offsetAt(x, z);

                assertEquals(bamboo.x() * 0.5F, dripstone.x(), TOLERANCE);
                assertEquals(bamboo.z() * 0.5F, dripstone.z(), TOLERANCE);
            }
        }
    }

    @Test
    void reproducesMeasuredWidthsForEveryThickness() {
        assertWidth(BedrockPointedDripstoneCollision.Thickness.TIP, 6.0D / 16.0D);
        assertWidth(BedrockPointedDripstoneCollision.Thickness.MERGE, 6.0D / 16.0D);
        assertWidth(BedrockPointedDripstoneCollision.Thickness.FRUSTUM, 8.0D / 16.0D);
        assertWidth(BedrockPointedDripstoneCollision.Thickness.MIDDLE, 10.0D / 16.0D);
        assertWidth(BedrockPointedDripstoneCollision.Thickness.BASE, 12.0D / 16.0D);
    }

    @Test
    void reproducesMeasuredTipHeights() {
        BlockPosition position = new BlockPosition(0, 100, 0);
        Aabb upward = BedrockPointedDripstoneCollision.boxAt(
                position,
                BedrockPointedDripstoneCollision.Thickness.TIP,
                false
        );
        Aabb hanging = BedrockPointedDripstoneCollision.boxAt(
                position,
                BedrockPointedDripstoneCollision.Thickness.TIP,
                true
        );

        assertEquals(100.0D, upward.minY(), TOLERANCE);
        assertEquals(100.0D + 11.0D / 16.0D, upward.maxY(), TOLERANCE);
        assertEquals(100.0D + 5.0D / 16.0D, hanging.minY(), TOLERANCE);
        assertEquals(101.0D, hanging.maxY(), TOLERANCE);
    }

    private static void assertWidth(
            BedrockPointedDripstoneCollision.Thickness thickness,
            double measuredWidth
    ) {
        Aabb box = BedrockPointedDripstoneCollision.boxAt(
                new BlockPosition(0, 100, 0),
                thickness,
                false
        );
        double expected = measuredWidth - 2.0D * FACE_INSET;
        assertEquals(expected, box.maxX() - box.minX(), TOLERANCE);
        assertEquals(expected, box.maxZ() - box.minZ(), TOLERANCE);
    }

    private record OffsetFixture(
            int blockX,
            int blockZ,
            float offsetX,
            float offsetZ
    ) {
    }
}
