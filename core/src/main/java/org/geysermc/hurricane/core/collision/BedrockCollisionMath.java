package org.geysermc.hurricane.core.collision;

final class BedrockCollisionMath {
    static final double FACE_INSET = (1.0D / 1024.0D) / 16.0D;

    private static final long GOLDEN_RATIO_64 = -7046029254386353131L;

    private BedrockCollisionMath() {
    }

    static Offset randomOffset(
            int blockX,
            int blockZ,
            float minimum,
            float maximum,
            int steps
    ) {
        long seed = positionHash(blockX, blockZ);
        Xoroshiro128PlusPlus random = new Xoroshiro128PlusPlus(
                mixStafford13(seed),
                mixStafford13(seed + GOLDEN_RATIO_64)
        );

        float offsetX = quantizedOffset(random.nextLong(), minimum, maximum, steps);
        random.nextLong(); // Bedrock draws X, Y, Z; the Y offset is consumed but not applied.
        float offsetZ = quantizedOffset(random.nextLong(), minimum, maximum, steps);
        return new Offset(offsetX, offsetZ);
    }

    // Reproduces Bedrock's own block-position hash (not Java's Mth.getSeed); the constants match
    // native Bedrock, verified against Bedrock Dedicated Server 1.26.32.2.
    private static long positionHash(int x, int z) {
        long value = (116_129_781L * z)
                ^ ((0x2FC20F00000001L * Integer.toUnsignedLong(x)) >> 32);
        long mixed = (value * (42_317_861L * value + 11L)) >>> 16;
        return (int) mixed ^ 0x6A09E667F3BCC909L;
    }

    private static long mixStafford13(long seed) {
        seed = (seed ^ seed >>> 30) * -4658895280553007687L;
        seed = (seed ^ seed >>> 27) * -7723592293110705685L;
        return seed ^ seed >>> 31;
    }

    private static float quantizedOffset(
            long random,
            float minimum,
            float maximum,
            int steps
    ) {
        float unit = (random >>> 40) * 5.9604645E-8F;
        float stepSize = (maximum - minimum) / (steps - 1);
        float step = (float) Math.floor(steps * unit);
        return minimum + step * stepSize;
    }

    record Offset(float x, float z) {
    }

    private static final class Xoroshiro128PlusPlus {
        private long seedLo;
        private long seedHi;

        private Xoroshiro128PlusPlus(long seedLo, long seedHi) {
            this.seedLo = seedLo;
            this.seedHi = seedHi;
        }

        private long nextLong() {
            long lo = seedLo;
            long hi = seedHi;
            long result = Long.rotateLeft(lo + hi, 17) + lo;
            hi ^= lo;
            seedLo = Long.rotateLeft(lo, 49) ^ hi ^ hi << 21;
            seedHi = Long.rotateLeft(hi, 28);
            return result;
        }
    }
}
