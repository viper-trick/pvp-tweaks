package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.CpsTracker;
import com.pvptweaks.zoom.ZoomManager;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
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
    private double unprocessedMouseX;

    @Shadow
    private double unprocessedMouseY;

    private boolean pvptweaks$originalSmoothCameraState;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void pvptweaks$onMouseButton(long window, MouseInput mouseInput, int action, CallbackInfo ci) {
        if (action == 1) {
            int btn = mouseInput.button();
            if (btn == 0 || btn == 1) {
                CpsTracker.registerClick(btn);
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
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            if (ZoomManager.isZooming() && cfg.zoomSmoothCamera) {
                this.client.options.smoothCameraEnabled = true;
            }
            if (cfg.sensitivityOverrideEnabled) {
                double factor = cfg.sensitivityOverride / 100.0;
                unprocessedMouseX *= factor;
                unprocessedMouseY *= factor;
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
