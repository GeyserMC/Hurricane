package org.geysermc.hurricane.core.math;

import java.util.Objects;

public record Aabb(
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
) {
    public Aabb {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("Minimum bounds must not exceed maximum bounds");
        }
    }

    public Aabb move(Movement movement) {
        Objects.requireNonNull(movement, "movement");
        return new Aabb(
                minX + movement.x(),
                minY + movement.y(),
                minZ + movement.z(),
                maxX + movement.x(),
                maxY + movement.y(),
                maxZ + movement.z()
        );
    }

    public Aabb union(Aabb other) {
        Objects.requireNonNull(other, "other");
        return new Aabb(
                Math.min(minX, other.minX),
                Math.min(minY, other.minY),
                Math.min(minZ, other.minZ),
                Math.max(maxX, other.maxX),
                Math.max(maxY, other.maxY),
                Math.max(maxZ, other.maxZ)
        );
    }

    public boolean overlaps(Aabb other) {
        Objects.requireNonNull(other, "other");
        return maxX > other.minX
                && minX < other.maxX
                && maxY > other.minY
                && minY < other.maxY
                && maxZ > other.minZ
                && minZ < other.maxZ;
    }

}
