package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    private boolean pvptweaks$originalSmoothCameraState;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void pvptweaks$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && client != null && client.options != null) {
            if (client.options.attackKey.matchesMouse(button)) {
                CpsTracker.registerClick(0);
            } else if (client.options.useKey.matchesMouse(button)) {
                CpsTracker.registerClick(1);
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomManager.isZooming()) {
            ZoomManager.onMouseScroll(vertical);
            ci.cancel();
        }
    }

    @Inject(method = "updateMouse", at = @At("HEAD"))
    private void pvptweaks$preUpdateMouse(double delta, CallbackInfo ci) {
        if (this.client != null && this.client.options != null) {
            pvptweaks$originalSmoothCameraState = this.client.options.smoothCameraEnabled;
            if (ZoomManager.isZooming()) {
                this.client.options.smoothCameraEnabled = true;
            }
        }
    }

    @Inject(method = "updateMouse", at = @At("RETURN"))
    private void pvptweaks$postUpdateMouse(double delta, CallbackInfo ci) {
        if (this.client != null && this.client.options != null) {
            this.client.options.smoothCameraEnabled = pvptweaks$originalSmoothCameraState;
        }
    }
}
