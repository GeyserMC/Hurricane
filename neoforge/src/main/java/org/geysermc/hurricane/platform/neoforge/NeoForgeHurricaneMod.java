package org.geysermc.hurricane.platform.neoforge;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.geysermc.hurricane.Hurricane;

@Mod("hurricane")
public class NeoForgeHurricaneMod extends Hurricane {

    public NeoForgeHurricaneMod() {
        super();
        NeoForge.EVENT_BUS.addListener(this::onServerStart);
    }

    private void onServerStart(ServerStartedEvent event) {
        super.onHurricaneInitialize();
    }

    @Override
    public void registerBlockPlaceEvent() {
        NeoForge.EVENT_BUS.addListener(this::onBlockPlace);
    }

    public void onBlockPlace(PlayerInteractEvent.RightClickBlock event) {
        Item item = event.getItemStack().getItem();
        if (item.equals(Items.BAMBOO)) {
            if (event.getPos().equals(event.getEntity().blockPosition())) {
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
        if (item.equals(Items.POINTED_DRIPSTONE)) {
            if (event.getPos().equals(event.getEntity().blockPosition().below())) {
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }
}
