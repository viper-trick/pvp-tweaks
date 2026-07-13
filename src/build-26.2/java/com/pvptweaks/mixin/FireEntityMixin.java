package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlameFeatureRenderer.class)
public class FireEntityMixin {

    @Inject(method = "renderSolid", at = @At("HEAD"), require = 0)
    private void pvptweaks$scaleFire(CallbackInfo ci) {
        float scale = PvpTweaksConfig.get().getFireEntityScale();
    }
}
