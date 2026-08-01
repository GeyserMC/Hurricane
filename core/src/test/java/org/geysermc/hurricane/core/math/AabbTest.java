package org.geysermc.hurricane.core.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AabbTest {
    private static final Aabb UNIT = new Aabb(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    @Test
    void overlapsWhenEveryAxisHasPositiveIntersection() {
        assertTrue(UNIT.overlaps(new Aabb(0.5, 0.25, 0.75, 1.5, 1.25, 1.75)));
    }

    @Test
    void faceContactIsNotOverlap() {
        assertFalse(UNIT.overlaps(new Aabb(1.0, 0.0, 0.0, 2.0, 1.0, 1.0)));
        assertFalse(UNIT.overlaps(new Aabb(0.0, 1.0, 0.0, 1.0, 2.0, 1.0)));
        assertFalse(UNIT.overlaps(new Aabb(0.0, 0.0, 1.0, 1.0, 1.0, 2.0)));
    }

    @Test
    void moveReturnsTranslatedBoxWithoutMutatingOriginal() {
        Aabb moved = UNIT.move(new Movement(2.5, -1.0, 0.25));

        assertNotSame(UNIT, moved);
        assertEquals(new Aabb(2.5, -1.0, 0.25, 3.5, 0.0, 1.25), moved);
        assertEquals(new Aabb(0.0, 0.0, 0.0, 1.0, 1.0, 1.0), UNIT);
    }

    @Test
    void unionUsesOuterBounds() {
        assertEquals(
                new Aabb(-2.0, 0.0, -3.0, 4.0, 5.0, 2.0),
                UNIT.union(new Aabb(-2.0, 2.0, -3.0, 4.0, 5.0, 2.0))
        );
    }

    @Test
    void rejectsReversedBoundsOnEveryAxis() {
        assertThrows(IllegalArgumentException.class, () -> new Aabb(1.0, 0.0, 0.0, 0.0, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Aabb(0.0, 1.0, 0.0, 1.0, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Aabb(0.0, 0.0, 1.0, 1.0, 1.0, 0.0));
    }
}
