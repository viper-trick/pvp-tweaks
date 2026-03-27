package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
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

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private static void pvptweaks$fireHead(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Sprite sprite,
            CallbackInfo ci) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        // Hide completely
        if ("none".equals(cfg.firePreset) || cfg.getFireOverlayScale() <= 0.0f) {
            ci.cancel();
            return;
        }

        float scale = cfg.getFireOverlayScale();
        if (Float.compare(scale, 1.0f) == 0) return; // vanilla, no changes

        // Push a scaled matrix around screen center before the method draws
        MinecraftClient mc = MinecraftClient.getInstance();
        float cx = mc.getWindow().getScaledWidth()  / 2f;
        float cy = mc.getWindow().getScaledHeight() / 2f;

        matrices.push();
        matrices.translate(cx, cy, 0f);
        matrices.scale(scale, scale, 1f);
        matrices.translate(-cx, -cy, 0f);
    }

    @Inject(method = "renderFireOverlay", at = @At("RETURN"), require = 0)
    private static void pvptweaks$fireTail(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Sprite sprite,
            CallbackInfo ci) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float scale = cfg.getFireOverlayScale();

        // Pop only if we pushed (scale != 0 and scale != 1)
        if ("none".equals(cfg.firePreset) || scale <= 0.0f || Float.compare(scale, 1.0f) == 0) return;

        matrices.pop();
    }
}
