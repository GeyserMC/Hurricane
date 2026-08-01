package org.geysermc.hurricane.modded;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import org.geysermc.hurricane.core.collision.BedrockPointedDripstoneCollision;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;

public final class MinecraftPointedDripstoneCollision {
    private MinecraftPointedDripstoneCollision() {
    }

    public static Aabb boxAt(
            BlockPosition position,
            SpeleothemThickness thickness,
            Direction tipDirection
    ) {
        return BedrockPointedDripstoneCollision.boxAt(
                position,
                toCore(thickness),
                Objects.requireNonNull(tipDirection, "tipDirection") == Direction.DOWN
        );
    }

    static BedrockPointedDripstoneCollision.Thickness toCore(
            SpeleothemThickness thickness
    ) {
        return switch (Objects.requireNonNull(thickness, "thickness")) {
            case TIP_MERGE -> BedrockPointedDripstoneCollision.Thickness.MERGE;
            case TIP -> BedrockPointedDripstoneCollision.Thickness.TIP;
            case FRUSTUM -> BedrockPointedDripstoneCollision.Thickness.FRUSTUM;
            case MIDDLE -> BedrockPointedDripstoneCollision.Thickness.MIDDLE;
            case BASE -> BedrockPointedDripstoneCollision.Thickness.BASE;
        };
    }
}
