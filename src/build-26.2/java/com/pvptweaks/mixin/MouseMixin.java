package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
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
    private Minecraft client;

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    private boolean pvptweaks$originalSmoothCameraState;

    // TODO 26.x: MouseHandler API was completely refactored — onMouseButton/onMouseScroll/updateMouse no longer exist
    // These mixins are disabled with require=0 until a proper 26.x hook is found

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"), require = 0)
    private void pvptweaks$preUpdateMouse(CallbackInfo ci) {
        if (this.client != null && this.client.options != null) {
            pvptweaks$originalSmoothCameraState = this.client.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                this.client.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"), require = 0)
    private void pvptweaks$postUpdateMouse(CallbackInfo ci) {
        if (this.client != null && this.client.options != null) {
            this.client.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
