package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class FireOverlayMixin {

    // Confirmed signature from mappings: method_23070
    // renderFireOverlay(MatrixStack, VertexConsumerProvider, Sprite)
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), require = 0)
    private static void pvptweaks$scaleFireOverlay(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Sprite sprite,
            CallbackInfo ci) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset)) { matrices.scale(0.001f, 0.001f, 1.0f); return; }
        float scale = cfg.getFireOverlayScale();
        if (scale <= 0.0f) {
            // Scale to zero = invisible
            matrices.scale(0.001f, 0.001f, 1.0f);
            return;
        }
        if (Float.compare(scale, 1.0f) != 0) {
            matrices.translate(0.5f, 0.5f, 0.0f);
            matrices.scale(scale, scale, 1.0f);
            matrices.translate(-0.5f, -0.5f, 0.0f);
        }
    }
}
