package com.pvptweaks.mixin;

import com.mojang.blaze3d.platform.Window;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.CrosshairRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class CrosshairMixin {

    @Inject(
        method = "renderCrosshair",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void pvptweaks$renderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.customCrosshairEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType().isFirstPerson()) {
            Window window = mc.getWindow();
            float scale = (float) window.getGuiScale();
            context.pose().pushMatrix();
            context.pose().scale(1.0f / scale, 1.0f / scale);
            int cx = window.getWidth() / 2;
            int cy = window.getHeight() / 2;
            float pixelScale = (float) window.getHeight() / 1080.0f;
            CrosshairRenderer.drawNative(context, cx, cy, cfg, pixelScale);
            context.pose().popMatrix();
        }
        ci.cancel();
    }
}
