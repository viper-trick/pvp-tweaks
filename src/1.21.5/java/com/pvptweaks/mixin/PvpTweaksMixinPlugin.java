package com.pvptweaks.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.Set;

public class PvpTweaksMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String simple = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        if (simple.equals("ZoomifyMixin") || targetClassName.contains("zoomify")) {
            return FabricLoader.getInstance().isModLoaded("zoomify");
        }
        // These mixins target 1.21.9+ APIs not present in 1.21.4
        if (simple.equals("EndCrystalEntityRendererMixin") ||
            simple.equals("FireEntityMixin") ||
            simple.equals("FireBlockRenderMixin") ||
            simple.equals("ExplosionPacketMixin")) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
