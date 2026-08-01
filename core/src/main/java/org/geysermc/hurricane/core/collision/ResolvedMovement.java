package org.geysermc.hurricane.core.collision;

import org.geysermc.hurricane.core.math.Movement;

public record ResolvedMovement(Movement movement, boolean collided) {
}
