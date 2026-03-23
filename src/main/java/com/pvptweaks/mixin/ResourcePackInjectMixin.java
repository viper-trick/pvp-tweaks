package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public class ResourcePackInjectMixin {

    // Must be LAST in the pack list so it overrides vanilla models
    @ModifyVariable(method = "reload", at = @At("HEAD"), argsOnly = true, index = 4)
    private List<ResourcePack> pvptweaks$injectFirePack(List<ResourcePack> original) {
        List<ResourcePack> mutable = new ArrayList<>(original);
        mutable.add(new PvpTweaksDynamicPack());
        com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] FirePack injected as pack #{}, preset={}",
            mutable.size(), com.pvptweaks.config.PvpTweaksConfig.get().firePreset);
        return mutable;
    }
}
