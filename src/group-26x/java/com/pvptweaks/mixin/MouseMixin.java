package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
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

    @Inject(method = "onButton", at = @At("HEAD"))
    private void pvptweaks$onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS) {
            int btn = buttonInfo.input();
            if (btn == 0 || btn == 1) {
                CpsTracker.registerClick(btn);
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"))
    private void pvptweaks$preUpdateMouse(CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            pvptweaks$originalSmoothCameraState = this.minecraft.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                this.minecraft.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"))
    private void pvptweaks$postUpdateMouse(CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            this.minecraft.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
