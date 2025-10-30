package org.geysermc.hurricane.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformUtilImpl {

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
