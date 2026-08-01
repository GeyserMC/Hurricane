package org.geysermc.hurricane.core.collision;

import java.util.Objects;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;

public final class BedrockPointedDripstoneCollision {
    private static final float OFFSET_MIN = -0.125F;
    private static final float OFFSET_MAX = 0.125F;
    private static final int OFFSET_STEPS = 16;

    private BedrockPointedDripstoneCollision() {
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

    public static Aabb boxAt(
            BlockPosition position,
            Thickness thickness,
            boolean hanging
    ) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(thickness, "thickness");

        Offset offset = offsetAt(position.x(), position.z());
        double halfWidth = thickness.width() * 0.5D;
        double centerX = position.x() + 0.5D + offset.x();
        double centerZ = position.z() + 0.5D + offset.z();
        double minimumY = position.y();
        double maximumY = position.y() + 1.0D;

        if (thickness == Thickness.TIP) {
            if (hanging) {
                minimumY += 5.0D / 16.0D;
            } else {
                maximumY -= 5.0D / 16.0D;
            }
        }

        return new Aabb(
                centerX - halfWidth + BedrockCollisionMath.FACE_INSET,
                minimumY,
                centerZ - halfWidth + BedrockCollisionMath.FACE_INSET,
                centerX + halfWidth - BedrockCollisionMath.FACE_INSET,
                maximumY,
                centerZ + halfWidth - BedrockCollisionMath.FACE_INSET
        );
    }

    public enum Thickness {
        TIP(6.0D / 16.0D),
        FRUSTUM(8.0D / 16.0D),
        MIDDLE(10.0D / 16.0D),
        BASE(12.0D / 16.0D),
        MERGE(6.0D / 16.0D);

        private final double width;

        Thickness(double width) {
            this.width = width;
        }

        double width() {
            return width;
        }
    }

    public record Offset(float x, float z) {
    }
}
