package org.geysermc.hurricane.paper;

import org.bukkit.util.BoundingBox;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.Movement;

public final class PaperBoxes {
    private PaperBoxes() {
    }

    public static Aabb toCore(BoundingBox box) {
        return new Aabb(
                box.getMinX(),
                box.getMinY(),
                box.getMinZ(),
                box.getMaxX(),
                box.getMaxY(),
                box.getMaxZ()
        );
    }

    public static BoundingBox toPaper(Aabb box) {
        return new BoundingBox(
                box.minX(),
                box.minY(),
                box.minZ(),
                box.maxX(),
                box.maxY(),
                box.maxZ()
        );
    }

    public static Aabb atPosition(
            BoundingBox currentBox,
            double currentX,
            double currentY,
            double currentZ,
            double targetX,
            double targetY,
            double targetZ
    ) {
        return toCore(currentBox).move(new Movement(
                targetX - currentX,
                targetY - currentY,
                targetZ - currentZ
        ));
    }

    public static Aabb deflate(Aabb box, double amount) {
        return new Aabb(
                box.minX() + amount,
                box.minY() + amount,
                box.minZ() + amount,
                box.maxX() - amount,
                box.maxY() - amount,
                box.maxZ() - amount
        );
    }
}
