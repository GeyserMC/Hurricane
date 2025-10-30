package org.geysermc.hurricane.util.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformUtilImpl {

    public static Path path() {
        return FMLPaths.CONFIGDIR.get();
    }
}
