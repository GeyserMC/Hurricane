package org.geysermc.hurricane.paper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.bukkit.util.BoundingBox;
import org.geysermc.hurricane.core.math.Aabb;
import org.junit.jupiter.api.Test;

class PaperCollisionWorldTest {
    @Test
    void translatesBlockLocalCollisionBoxesIntoWorldCoordinates() {
        List<BoundingBox> localShape = List.of(
                new BoundingBox(0.4D, 0.0D, 0.4D, 0.6D, 1.0D, 0.6D)
        );
        Aabb targetAtBlock = new Aabb(
                410.45D, 161.0D, 408.45D,
                410.55D, 162.0D, 408.55D
        );

        assertTrue(PaperCollisionWorld.worldShapeOverlaps(
                localShape,
                410,
                161,
                408,
                targetAtBlock
        ));
        assertFalse(PaperCollisionWorld.worldShapeOverlaps(
                localShape,
                409,
                161,
                408,
                targetAtBlock
        ));
    }

    @Test
    void faceContactDoesNotCountAsWorldShapeOverlap() {
        List<BoundingBox> localShape = List.of(
                new BoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
        );
        Aabb touching = new Aabb(
                411.0D, 161.0D, 408.0D,
                412.0D, 162.0D, 409.0D
        );

        assertFalse(PaperCollisionWorld.worldShapeOverlaps(
                localShape,
                410,
                161,
                408,
                touching
        ));
    }
}
