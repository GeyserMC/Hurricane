package org.geysermc.hurricane;

import org.geysermc.hurricane.config.HurricaneConfiguration;
import org.geysermc.hurricane.mixin.MixinConfigPlugin;
import org.geysermc.hurricane.util.HurricaneModLogger;

public abstract class HurricaneMod implements HurricanePlatform {

	protected final HurricaneLogger logger = new HurricaneModLogger();

	@Override
	public HurricaneLogger logger() {
		return logger;
	}

	@Override
	public HurricaneConfiguration configuration() {
		// We load the config very early (during the mixin apply stage), so we retrieve it here
		return MixinConfigPlugin.getConfig();
	}

	@Override
	public void verifyConfig(HurricaneConfiguration configuration) {
		if (configuration.collisionFixes().bamboo() && !BedrockUtils.isGeyserOrFloodgateInstalled()) {
			logger.warn("Bamboo fix is enabled, but Geyser or Floodgate are not found! Without these mods, Hurricane cannot " +
					"fix the bamboo lag-back for Bedrock players.");
		}
	}
}