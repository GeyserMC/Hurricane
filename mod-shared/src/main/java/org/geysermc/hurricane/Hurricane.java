package org.geysermc.hurricane;

import org.geysermc.hurricane.mixin.MixinConfigPlugin;
import org.geysermc.hurricane.config.Config;
import org.geysermc.hurricane.util.BedrockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Hurricane {
	public static final Logger LOGGER = LoggerFactory.getLogger("Hurricane");
	private final Config config = MixinConfigPlugin.getConfig();

	protected void onHurricaneInitialize() {
		if (config.isBamboo() && !BedrockUtils.isGeyserOrFloodgateInstalled()) {
			LOGGER.warn("Bamboo fix is enabled, but Geyser or Floodgate are not found! Without these mods, Hurricane cannot " +
					"fix the bamboo lag-back for Bedrock players.");
		}

		if (config.isBamboo() || config.isPointedDripstone()) {
			registerBlockPlaceEvent();
			LOGGER.debug("BlockPlaceEvent registered, as the Bamboo or Pointed Dripstone fix is enabled.");
		}

		LOGGER.info("Started Hurricane!");
	}

	public abstract void registerBlockPlaceEvent();
}