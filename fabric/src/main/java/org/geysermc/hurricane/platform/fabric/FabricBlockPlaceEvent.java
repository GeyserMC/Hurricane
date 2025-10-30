package org.geysermc.hurricane.platform.fabric;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FabricBlockPlaceEvent {

    // TODO: proper checks
    public void registerInteractBlockCallback() {
        UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
            Item item = player.getItemInHand(hand).getItem();
            if (item.equals(Items.BAMBOO)) {
                if (blockHitResult.getBlockPos().equals(player.blockPosition())) {
                    return InteractionResult.FAIL;
                }
            }
            if (item.equals(Items.POINTED_DRIPSTONE)) {
                if (blockHitResult.getBlockPos().equals(player.blockPosition().below())) {
                    //you can place dripstone in yourself
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
