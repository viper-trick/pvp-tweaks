package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class ResourcePackInjectMixin {

    // Must be LAST in the pack list so it overrides vanilla models
    @ModifyVariable(method = "createReload", at = @At("HEAD"), argsOnly = true, index = 4)
    private List<PackResources> pvptweaks$injectFirePack(List<PackResources> original) {
        List<PackResources> mutable = new ArrayList<>(original);
        mutable.add(new PvpTweaksDynamicPack());
        com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] DynamicPack injected into ResourceManager. Pack count: {}",
            mutable.size());
        return mutable;
    }
}
