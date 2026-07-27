package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Shadow @Final private Minecraft minecraft;

    private boolean pvptweaks$originalSmoothCameraState;

    @Inject(method = "onButton", at = @At("HEAD"))
    private void pvptweaks$onMouseButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS && minecraft != null && minecraft.options != null) {
            MouseButtonEvent click = new MouseButtonEvent(0.0, 0.0, info);
            if (minecraft.options.keyAttack.matchesMouse(click)) {
                CpsTracker.registerClick(0);
            } else if (minecraft.options.keyUse.matchesMouse(click)) {
                CpsTracker.registerClick(1);
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"), require = 0)
    private void pvptweaks$preUpdateMouse(CallbackInfo ci) {
        if (minecraft != null && minecraft.options != null) {
            pvptweaks$originalSmoothCameraState = minecraft.options.smoothCamera;
            if (ZoomManager.isZooming()) {
                minecraft.options.smoothCamera = true;
            }
        }
    }

    @Inject(method = "handleAccumulatedMovement", at = @At("RETURN"), require = 0)
    private void pvptweaks$postUpdateMouse(CallbackInfo ci) {
        if (minecraft != null && minecraft.options != null) {
            minecraft.options.smoothCamera = pvptweaks$originalSmoothCameraState;
        }
    }
}
