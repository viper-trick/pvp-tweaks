package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$modifyFov(Camera camera, float tickDelta, boolean useFovSetting, CallbackInfoReturnable<Float> cir) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.fovOverrideEnabled) {
            cir.setReturnValue((float) cfg.fovOverride);
            return;
        }
        float zoom = com.pvptweaks.zoom.ZoomManager.getSmoothZoom(tickDelta);
        if (zoom > 1.0f) {
            cir.setReturnValue(cir.getReturnValue() / zoom);
        }
    }
}
