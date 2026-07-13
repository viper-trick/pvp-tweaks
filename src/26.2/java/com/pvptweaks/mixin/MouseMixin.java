package com.pvptweaks.mixin;

import com.pvptweaks.zoom.ZoomManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    private boolean pvptweaks$originalSmoothCameraState;

    // TODO 26.x: MouseHandler API was completely refactored — onMouseButton/onMouseScroll/updateMouse no longer exist
    // These mixins are disabled with require=0 until a proper 26.x hook is found

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"), require = 0)
    private void pvptweaks$preUpdateMouse(CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            pvptweaks$originalSmoothCameraState = this.minecraft.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                this.minecraft.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"), require = 0)
    private void pvptweaks$postUpdateMouse(CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            this.minecraft.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
