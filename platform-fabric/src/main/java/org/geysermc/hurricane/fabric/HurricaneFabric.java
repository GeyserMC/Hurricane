package org.geysermc.hurricane.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.geysermc.hurricane.modded.HurricaneModded;

public final class HurricaneFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> HurricaneModded.initialize()
        );
    }
}
