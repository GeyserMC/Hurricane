package org.geysermc.hurricane.modded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.geysermc.hurricane.core.collision.BedrockBambooCollision;
import org.geysermc.hurricane.core.math.BlockPosition;
import org.geysermc.hurricane.modded.HurricaneModded;
import org.geysermc.hurricane.modded.MinecraftBoxes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooStalkBlock.class)
public final class BambooStalkBlockMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void hurricane$useBedrockCollision(
            BlockState state,
            BlockGetter level,
            BlockPos position,
            CollisionContext context,
            CallbackInfoReturnable<VoxelShape> callback
    ) {
        if (!(context instanceof EntityCollisionContext entityContext)
                || !(entityContext.getEntity() instanceof Player player)
                || !HurricaneModded.isBedrockPlayer(player.getUUID())) {
            return;
        }

        BlockPosition corePosition =
                new BlockPosition(position.getX(), position.getY(), position.getZ());
        callback.setReturnValue(MinecraftBoxes.toLocalShape(
                BedrockBambooCollision.boxAt(corePosition),
                corePosition
        ));
    }
}
