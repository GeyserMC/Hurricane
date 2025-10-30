package org.geysermc.hurricane.mixin;

import org.geysermc.hurricane.config.ConfigLoader;
import org.geysermc.hurricane.config.HurricaneConfiguration;
import org.geysermc.hurricane.util.PlatformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {

    private static final HurricaneConfiguration config;

    public static HurricaneConfiguration getConfig() {
        return config;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch (mixinClassName) {
            case "org.geysermc.hurricane.mixin.BambooBlockMixin" -> config.collisionFixes().bamboo();
            case "org.geysermc.hurricane.mixin.PointedDripstoneBlockMixin" -> config.collisionFixes().pointedDripstone();
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    static {
        try {
            config = ConfigLoader.loadConfig(PlatformUtil.configPath());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

}