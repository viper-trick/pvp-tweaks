package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.CrosshairRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CrosshairMixin {

    @Inject(
        method = "renderCrosshair",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void pvptweaks$renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.customCrosshairEnabled) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.getPerspective().isFirstPerson()) {
            Window window = mc.getWindow();
            float scale = (float) window.getScaleFactor();
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(1.0f / scale, 1.0f / scale);
            int cx = window.getFramebufferWidth() / 2;
            int cy = window.getFramebufferHeight() / 2;
            float cs2Scale = (float) window.getWidth() / 640.0f;
            CrosshairRenderer.drawNative(context, cx, cy, cfg, cs2Scale);
            context.getMatrices().popMatrix();
        }
        ci.cancel();
    }
}
