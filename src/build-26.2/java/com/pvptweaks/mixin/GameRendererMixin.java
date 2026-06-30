package com.pvptweaks.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$modifyFov(CallbackInfoReturnable<Float> cir) {
        float zoom = com.pvptweaks.zoom.ZoomManager.getSmoothZoom(0.0f);
        if (zoom > 1.0f) {
            cir.setReturnValue(cir.getReturnValue() / zoom);
        }
    }
}
