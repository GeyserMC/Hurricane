package org.geysermc.hurricane.modded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.geysermc.hurricane.modded.HurricaneModded;
import org.geysermc.hurricane.modded.MinecraftBoxes;
import org.geysermc.hurricane.modded.MinecraftPointedDripstoneCollision;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    // PointedDripstoneBlock inherits getCollisionShape from BlockBehaviour rather than declaring
    // it, so this mixin adds the override directly.
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos position,
            CollisionContext context
    ) {
        if (!(context instanceof EntityCollisionContext entityContext)
                || !(entityContext.getEntity() instanceof Player player)
                || !HurricaneModded.isBedrockPlayer(player.getUUID())) {
            return state.getShape(level, position);
        }

        BlockPosition corePosition =
                new BlockPosition(position.getX(), position.getY(), position.getZ());
        return MinecraftBoxes.toLocalShape(
                MinecraftPointedDripstoneCollision.boxAt(
                        corePosition,
                        state.getValue(SpeleothemBlock.THICKNESS),
                        state.getValue(SpeleothemBlock.TIP_DIRECTION)
                ),
                corePosition
        );
    }
}
