package org.geysermc.hurricane.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.geysermc.hurricane.util.BedrockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooStalkBlock.class)
public class BambooBlockMixin {

	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		// We have a collision context here, so we can make this apply only for Bedrock players.
		if (collisionContext instanceof EntityCollisionContext entityShapeContext
				&& entityShapeContext.getEntity() instanceof Player player
				&& BedrockUtils.isBedrockPlayer(player.getUUID())) {
			cir.setReturnValue(Shapes.empty());
		}
	}
}