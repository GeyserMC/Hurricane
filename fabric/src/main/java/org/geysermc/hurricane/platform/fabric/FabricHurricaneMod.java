package org.geysermc.hurricane.platform.fabric;

import net.fabricmc.api.ModInitializer;
import org.geysermc.hurricane.HurricaneMod;
import org.geysermc.hurricane.config.HurricaneConfiguration;
import org.geysermc.hurricane.mixin.MixinConfigPlugin;

public class FabricHurricaneMod extends HurricaneMod implements ModInitializer {

	@Override
	public void onInitialize() {
		enable(configuration());
	}

	@Override
	public void registerCollisionFixes(HurricaneConfiguration configuration) {
		FabricBlockPlaceEvent.registerInteractBlockCallback();
	}
}