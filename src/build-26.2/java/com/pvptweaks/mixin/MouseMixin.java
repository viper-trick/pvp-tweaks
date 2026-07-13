package com.pvptweaks.mixin;

import com.pvptweaks.zoom.ZoomManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    private boolean pvptweaks$originalSmoothCameraState;

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"), require = 0)
    private void pvptweaks$preUpdateMouse(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client != null && client.options != null) {
            pvptweaks$originalSmoothCameraState = client.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                client.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"), require = 0)
    private void pvptweaks$postUpdateMouse(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client != null && client.options != null) {
            client.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
