package org.geysermc.hurricane.modded;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
import org.geysermc.hurricane.core.collision.BedrockBambooCollision;
import org.junit.jupiter.api.Test;

class BedrockBambooMinecraftParityTest {
    private static final float OFFSET_MIN = -0.25F;
    private static final float OFFSET_MAX = 0.25F;
    private static final int OFFSET_STEPS = 16;

    @Test
    void matchesSuppliedBedrockAlgorithmUsingMinecraftRandomClasses() {
        for (int x = -64; x <= 64; x++) {
            for (int z = -64; z <= 64; z++) {
                assertOffset(x, z);
            }
        }

        int[] extremes = {
                Integer.MIN_VALUE,
                Integer.MIN_VALUE + 1,
                -30_000_000,
                -1,
                0,
                1,
                30_000_000,
                Integer.MAX_VALUE - 1,
                Integer.MAX_VALUE
        };
        for (int x : extremes) {
            for (int z : extremes) {
                assertOffset(x, z);
            }
        }
    }

    private static void assertOffset(int x, int z) {
        BedrockBambooCollision.Offset expected = referenceOffset(x, z);
        BedrockBambooCollision.Offset actual = BedrockBambooCollision.offsetAt(x, z);

        assertEquals(expected.x(), actual.x(), "x offset at " + x + ", " + z);
        assertEquals(expected.z(), actual.z(), "z offset at " + x + ", " + z);
    }

    private static BedrockBambooCollision.Offset referenceOffset(int x, int z) {
        long seed = positionHash(x, z);
        Xoroshiro128PlusPlus random = new Xoroshiro128PlusPlus(
                RandomSupport.mixStafford13(seed),
                RandomSupport.mixStafford13(seed + RandomSupport.GOLDEN_RATIO_64)
        );

        float offsetX = calculateOffsetValue(randomToFloat(random.nextLong()));
        random.nextLong();
        float offsetZ = calculateOffsetValue(randomToFloat(random.nextLong()));
        return new BedrockBambooCollision.Offset(offsetX, offsetZ);
    }

    private static long positionHash(int x, int z) {
        long value = (116_129_781L * z)
                ^ ((0x2FC20F00000001L * Integer.toUnsignedLong(x)) >> 32);
        long mixed = (value * (42_317_861L * value + 11L)) >>> 16;
        return (int) mixed ^ 0x6A09E667F3BCC909L;
    }

    private static float randomToFloat(long random) {
        return (random >>> 40) * 5.9604645E-8F;
    }

    private static float calculateOffsetValue(float random) {
        float range = OFFSET_MAX - OFFSET_MIN;
        float stepSize = range / (OFFSET_STEPS - 1);
        float index = (float) Math.floor(OFFSET_STEPS * random);
        return OFFSET_MIN + index * stepSize;
    }
}
