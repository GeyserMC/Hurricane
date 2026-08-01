package org.geysermc.hurricane.core.collision;

import java.util.List;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.Movement;

public final class MovementResolver {
    private MovementResolver() {
    }

    public static ResolvedMovement resolve(
            Aabb start,
            Aabb requestedEnd,
            List<Aabb> obstacles
    ) {
        double requestedX = requestedEnd.minX() - start.minX();
        double requestedY = requestedEnd.minY() - start.minY();
        double requestedZ = requestedEnd.minZ() - start.minZ();
        Aabb moved = start;

        double resolvedY = clipY(moved, obstacles, requestedY);
        moved = moved.move(new Movement(0.0D, resolvedY, 0.0D));

        double resolvedX;
        double resolvedZ;
        if (Math.abs(requestedX) < Math.abs(requestedZ)) {
            resolvedZ = clipZ(moved, obstacles, requestedZ);
            moved = moved.move(new Movement(0.0D, 0.0D, resolvedZ));
            resolvedX = clipX(moved, obstacles, requestedX);
        } else {
            resolvedX = clipX(moved, obstacles, requestedX);
            moved = moved.move(new Movement(resolvedX, 0.0D, 0.0D));
            resolvedZ = clipZ(moved, obstacles, requestedZ);
        }

        Movement resolved = new Movement(resolvedX, resolvedY, resolvedZ);
        boolean collided = Double.compare(resolvedX, requestedX) != 0
                || Double.compare(resolvedY, requestedY) != 0
                || Double.compare(resolvedZ, requestedZ) != 0;
        return new ResolvedMovement(resolved, collided);
    }

    private static double clipX(Aabb moving, List<Aabb> obstacles, double requested) {
        double resolved = requested;
        for (Aabb obstacle : obstacles) {
            if (!overlaps(moving.minY(), moving.maxY(), obstacle.minY(), obstacle.maxY())
                    || !overlaps(moving.minZ(), moving.maxZ(), obstacle.minZ(), obstacle.maxZ())) {
                continue;
            }

            if (resolved > 0.0D && moving.maxX() <= obstacle.minX()) {
                resolved = Math.min(resolved, obstacle.minX() - moving.maxX());
            } else if (resolved < 0.0D && moving.minX() >= obstacle.maxX()) {
                resolved = Math.max(resolved, obstacle.maxX() - moving.minX());
            }
        }
        return resolved;
    }

    private static double clipY(Aabb moving, List<Aabb> obstacles, double requested) {
        double resolved = requested;
        for (Aabb obstacle : obstacles) {
            if (!overlaps(moving.minX(), moving.maxX(), obstacle.minX(), obstacle.maxX())
                    || !overlaps(moving.minZ(), moving.maxZ(), obstacle.minZ(), obstacle.maxZ())) {
                continue;
            }

            if (resolved > 0.0D && moving.maxY() <= obstacle.minY()) {
                resolved = Math.min(resolved, obstacle.minY() - moving.maxY());
            } else if (resolved < 0.0D && moving.minY() >= obstacle.maxY()) {
                resolved = Math.max(resolved, obstacle.maxY() - moving.minY());
            }
        }
        return resolved;
    }

    private static double clipZ(Aabb moving, List<Aabb> obstacles, double requested) {
        double resolved = requested;
        for (Aabb obstacle : obstacles) {
            if (!overlaps(moving.minX(), moving.maxX(), obstacle.minX(), obstacle.maxX())
                    || !overlaps(moving.minY(), moving.maxY(), obstacle.minY(), obstacle.maxY())) {
                continue;
            }

            if (resolved > 0.0D && moving.maxZ() <= obstacle.minZ()) {
                resolved = Math.min(resolved, obstacle.minZ() - moving.maxZ());
            } else if (resolved < 0.0D && moving.minZ() >= obstacle.maxZ()) {
                resolved = Math.max(resolved, obstacle.maxZ() - moving.minZ());
            }
        }
        return resolved;
    }

    private static boolean overlaps(
            double firstMin,
            double firstMax,
            double secondMin,
            double secondMax
    ) {
        return firstMax > secondMin && firstMin < secondMax;
    }
}
