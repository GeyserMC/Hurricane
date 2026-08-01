package org.geysermc.hurricane.core.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.junit.jupiter.api.Test;

/**
 * The fixtures below are Bedrock values, not Java ones. They were measured against native
 * {@code minecraft:random_offset} bamboo on Bedrock Dedicated Server 1.26.32.2, and describe
 * the collision box the Bedrock client renders, which is what this class reproduces.
 */
class BedrockBambooCollisionTest {
    private static final float FLOAT_TOLERANCE = 0.0000001F;
    private static final double DOUBLE_TOLERANCE = 0.0000001D;
    private static final List<OffsetFixture> MEASURED_OFFSETS = List.of(
            new OffsetFixture(410, 408, 0.18333334F, 0.08333333F),
            new OffsetFixture(-8, -8, 0.11666667F, -0.01666667F),
            new OffsetFixture(-1, 0, -0.08333334F, 0.25F)
    );
    private static final BoxFixture MEASURED_WORLD_BOX = new BoxFixture(
            410,
            161,
            408,
            410.68339437246323D,
            161.0D,
            408.58339436352253D,
            410.87077230215073D,
            162.0D,
            408.77077229321003D
    );

    @Test
    void reproducesEveryMeasuredOffset() {
        for (OffsetFixture fixture : MEASURED_OFFSETS) {
            BedrockBambooCollision.Offset actual =
                    BedrockBambooCollision.offsetAt(fixture.blockX(), fixture.blockZ());

            assertEquals(fixture.offsetX(), actual.x(), FLOAT_TOLERANCE, fixture.toString());
            assertEquals(fixture.offsetZ(), actual.z(), FLOAT_TOLERANCE, fixture.toString());
        }
    }

    @Test
    void reproducesMeasuredAsymmetricWorldBox() {
        BoxFixture fixture = MEASURED_WORLD_BOX;
        Aabb actual = BedrockBambooCollision.boxAt(
                new BlockPosition(fixture.blockX(), fixture.blockY(), fixture.blockZ())
        );

        assertEquals(fixture.minX(), actual.minX(), DOUBLE_TOLERANCE);
        assertEquals(fixture.minY(), actual.minY(), DOUBLE_TOLERANCE);
        assertEquals(fixture.minZ(), actual.minZ(), DOUBLE_TOLERANCE);
        assertEquals(fixture.maxX(), actual.maxX(), DOUBLE_TOLERANCE);
        assertEquals(fixture.maxY(), actual.maxY(), DOUBLE_TOLERANCE);
        assertEquals(fixture.maxZ(), actual.maxZ(), DOUBLE_TOLERANCE);
    }

    @Test
    void everyTowerBlockUsesTheSameHorizontalOffset() {
        Aabb base = BedrockBambooCollision.boxAt(new BlockPosition(410, -64, 408));

        for (int y : new int[]{-63, 0, 161, 319}) {
            Aabb other = BedrockBambooCollision.boxAt(new BlockPosition(410, y, 408));
            assertEquals(base.minX(), other.minX(), DOUBLE_TOLERANCE);
            assertEquals(base.maxX(), other.maxX(), DOUBLE_TOLERANCE);
            assertEquals(base.minZ(), other.minZ(), DOUBLE_TOLERANCE);
            assertEquals(base.maxZ(), other.maxZ(), DOUBLE_TOLERANCE);
        }
    }

    @Test
    void offsetsAreQuantizedToSixteenInclusiveSteps() {
        for (int x = -32; x <= 32; x++) {
            for (int z = -32; z <= 32; z++) {
                BedrockBambooCollision.Offset offset = BedrockBambooCollision.offsetAt(x, z);
                assertQuantized(offset.x());
                assertQuantized(offset.z());
            }
        }
    }

    private static void assertQuantized(float offset) {
        float step = 0.5F / 15.0F;
        float index = (offset + 0.25F) / step;
        assertTrue(offset >= -0.25F && offset <= 0.25F, "range: " + offset);
        assertEquals(Math.rint(index), index, 0.00001D, "step: " + offset);
    }

    private record OffsetFixture(int blockX, int blockZ, float offsetX, float offsetZ) {
    }

    private record BoxFixture(
            int blockX,
            int blockY,
            int blockZ,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
    }
}
