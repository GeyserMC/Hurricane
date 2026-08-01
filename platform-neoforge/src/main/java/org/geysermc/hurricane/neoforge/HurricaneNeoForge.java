package org.geysermc.hurricane.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.geysermc.hurricane.modded.HurricaneModded;

@Mod("hurricane")
public final class HurricaneNeoForge {
    public HurricaneNeoForge() {
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void onServerStarted(ServerStartedEvent event) {
        HurricaneModded.initialize();
    }
}
