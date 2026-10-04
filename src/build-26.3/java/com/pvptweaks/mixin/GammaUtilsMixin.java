package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "io.github.sjouwer.gammautils.GammaManager", remap = false)
public class GammaUtilsMixin {

    @Inject(method = "setDynamicGamma", at = @At("HEAD"), cancellable = true)
    private static void pvptweaks$onSetDynamicGamma(CallbackInfo ci) {
        if (!"gammautils".equals(PvpTweaksConfig.get().fullbrightManagementMode)) {
            ci.cancel();
        }
    }

    @Inject(method = "setGamma", at = @At("HEAD"), cancellable = true)
    private static void pvptweaks$onSetGamma(CallbackInfo ci) {
        if (!"gammautils".equals(PvpTweaksConfig.get().fullbrightManagementMode)) {
            ci.cancel();
        }
    }
}
