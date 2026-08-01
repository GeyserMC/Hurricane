package org.geysermc.hurricane.core.collision;

import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;

public final class BedrockBambooCollision {
    private static final float OFFSET_MIN = -0.25F;
    private static final float OFFSET_MAX = 0.25F;
    private static final int OFFSET_STEPS = 16;
    private static final double STALK_WIDTH = 3.0D / 16.0D;

    private BedrockBambooCollision() {
    }

    public static Offset offsetAt(int blockX, int blockZ) {
        BedrockCollisionMath.Offset offset = BedrockCollisionMath.randomOffset(
                blockX,
                blockZ,
                OFFSET_MIN,
                OFFSET_MAX,
                OFFSET_STEPS
        );
        return new Offset(offset.x(), offset.z());
    }

    public static Aabb boxAt(BlockPosition position) {
        Offset offset = offsetAt(position.x(), position.z());
        double exactMinX = position.x() + 0.5D + offset.x();
        double exactMinZ = position.z() + 0.5D + offset.z();

        return new Aabb(
                exactMinX + BedrockCollisionMath.FACE_INSET,
                position.y(),
                exactMinZ + BedrockCollisionMath.FACE_INSET,
                exactMinX + STALK_WIDTH - BedrockCollisionMath.FACE_INSET,
                position.y() + 1.0D,
                exactMinZ + STALK_WIDTH - BedrockCollisionMath.FACE_INSET
        );
    }

    public record Offset(float x, float z) {
    }
}
