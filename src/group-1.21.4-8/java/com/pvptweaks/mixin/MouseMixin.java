package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
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

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    private boolean pvptweaks$originalSmoothCameraState;

    @Inject(method = "onPress", at = @At("HEAD"))
    private void pvptweaks$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && minecraft != null && minecraft.options != null) {
            if (minecraft.options.keyAttack.matchesMouse(button)) {
                CpsTracker.registerClick(0);
            } else if (minecraft.options.keyUse.matchesMouse(button)) {
                CpsTracker.registerClick(1);
            }
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomManager.isZooming()) {
            ZoomManager.onMouseScroll(vertical);
            ci.cancel();
        }
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void pvptweaks$preUpdateMouse(double delta, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            pvptweaks$originalSmoothCameraState = this.minecraft.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                this.minecraft.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "turnPlayer", at = @At("RETURN"))
    private void pvptweaks$postUpdateMouse(double delta, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.options != null) {
            this.minecraft.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
