package org.geysermc.hurricane.core.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.geysermc.hurricane.core.math.Movement;
import org.junit.jupiter.api.Test;

class MovementResolverTest {
    private static final double TOLERANCE = 0.0000001D;

    @Test
    void preservesClearTravel() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 0.6, 1.8, 0.6);
        Movement requested = new Movement(0.25, 0.0, 0.5);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(new Aabb(5.0, 0.0, 5.0, 6.0, 1.0, 6.0))
        );

        assertEquals(requested, resolved.movement());
        assertFalse(resolved.collided());
    }

    @Test
    void clipsDirectHorizontalImpactAtObstacleFace() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 0.6, 1.8, 0.6);
        Movement requested = new Movement(0.8, 0.0, 0.0);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(new Aabb(1.0, 0.0, 0.0, 1.2, 1.0, 0.6))
        );

        assertEquals(0.4, resolved.movement().x(), TOLERANCE);
        assertEquals(0.0, resolved.movement().z(), TOLERANCE);
        assertTrue(resolved.collided());
    }

    @Test
    void preventsCrossingEntireObstacleInOneMove() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 0.6, 1.8, 0.6);
        Movement requested = new Movement(4.0, 0.0, 0.0);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(new Aabb(1.0, 0.0, 0.0, 1.2, 1.0, 0.6))
        );

        assertEquals(0.4, resolved.movement().x(), TOLERANCE);
        assertTrue(resolved.collided());
    }

    @Test
    void clipsVerticalImpactBeforeHorizontalAxes() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 0.6, 1.0, 0.6);
        Movement requested = new Movement(0.25, 1.0, 0.0);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(new Aabb(0.0, 1.5, 0.0, 0.6, 2.0, 0.6))
        );

        assertEquals(0.5, resolved.movement().y(), TOLERANCE);
        assertEquals(0.25, resolved.movement().x(), TOLERANCE);
        assertTrue(resolved.collided());
    }

    @Test
    void permitsMovementThatEscapesExistingOverlap() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 0.6, 1.8, 0.6);
        Movement requested = new Movement(-0.5, 0.0, 0.0);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(new Aabb(0.4, 0.0, 0.0, 0.8, 1.0, 0.6))
        );

        assertEquals(requested, resolved.movement());
        assertFalse(resolved.collided());
    }

    @Test
    void stackedTowerBoxesActAsOneContinuousObstacle() {
        Aabb lower = BedrockBambooCollision.boxAt(new BlockPosition(410, 161, 408));
        Aabb upper = BedrockBambooCollision.boxAt(new BlockPosition(410, 162, 408));
        Aabb start = playerBox(lower.minX() - 0.31, 161.0, lower.minZ() + 0.05);
        Movement requested = new Movement(0.1, 0.0, 0.0);

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                start.move(requested),
                List.of(lower, upper)
        );

        assertEquals(0.01, resolved.movement().x(), TOLERANCE);
        assertTrue(resolved.collided());
    }

    @Test
    void permitsLoggedCornerMovementWhenFirstAxisClearsBamboo() {
        Aabb start = playerBox(410.42917D, 161.0D, 408.28336D);
        Aabb end = playerBox(410.34440D, 161.0D, 408.33252D);
        Aabb lower = BedrockBambooCollision.boxAt(new BlockPosition(410, 161, 408));
        Aabb upper = BedrockBambooCollision.boxAt(new BlockPosition(410, 162, 408));

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                end,
                List.of(lower, upper)
        );

        assertFalse(resolved.collided());
        assertEquals(end.minX() - start.minX(), resolved.movement().x(), TOLERANCE);
        assertEquals(end.minZ() - start.minZ(), resolved.movement().z(), TOLERANCE);
    }

    @Test
    void preservesTangentialMovementWhenInwardAxisIsClipped() {
        Aabb start = playerBox(410.42917D, 161.0D, 408.28336D);
        Aabb end = playerBox(410.40917D, 161.0D, 408.33252D);
        Aabb lower = BedrockBambooCollision.boxAt(new BlockPosition(410, 161, 408));
        Aabb upper = BedrockBambooCollision.boxAt(new BlockPosition(410, 162, 408));

        ResolvedMovement resolved = MovementResolver.resolve(
                start,
                end,
                List.of(lower, upper)
        );

        assertTrue(resolved.collided());
        assertEquals(end.minX() - start.minX(), resolved.movement().x(), TOLERANCE);
        assertEquals(lower.minZ() - start.maxZ(), resolved.movement().z(), TOLERANCE);
    }

    @Test
    void resolvesZBeforeXWhenZMagnitudeIsGreater() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        Movement requested = new Movement(1.0, 0.0, 2.0);
        Aabb corner = new Aabb(1.5, 0.0, 1.5, 2.5, 1.0, 2.5);

        ResolvedMovement resolved =
                MovementResolver.resolve(start, start.move(requested), List.of(corner));

        assertEquals(0.5, resolved.movement().x(), TOLERANCE);
        assertEquals(2.0, resolved.movement().z(), TOLERANCE);
    }

    @Test
    void resolvesXBeforeZWhenXMagnitudeIsGreaterOrEqual() {
        Aabb start = new Aabb(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        Movement requested = new Movement(2.0, 0.0, 1.0);
        Aabb corner = new Aabb(1.5, 0.0, 1.5, 2.5, 1.0, 2.5);

        ResolvedMovement resolved =
                MovementResolver.resolve(start, start.move(requested), List.of(corner));

        assertEquals(2.0, resolved.movement().x(), TOLERANCE);
        assertEquals(0.5, resolved.movement().z(), TOLERANCE);
    }

    private static Aabb playerBox(double x, double y, double z) {
        return new Aabb(
                x - 0.3D,
                y,
                z - 0.3D,
                x + 0.3D,
                y + 1.8D,
                z + 0.3D
        );
    }
}
