package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class FireOverlayMixin {

    @Inject(method = "renderFire",
            at = @At("HEAD"), cancellable = true, require = 0)
    private static void pvptweaks$fireHead(PoseStack matrices, MultiBufferSource vertexConsumers, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float scale = cfg.getFireOverlayScale();

        if (scale <= 0.0f) {
            ci.cancel();
            return;
        }

        matrices.pushPose();
        float translation = (1.0f - scale) * -0.5f;
        matrices.translate(0.0, translation, 0.0);
        matrices.scale(scale, scale, 1.0f);
    }

    @Inject(method = "renderFire",
            at = @At("RETURN"), require = 0)
    private static void pvptweaks$fireTail(PoseStack matrices, MultiBufferSource vertexConsumers, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float scale = cfg.getFireOverlayScale();

        if (scale <= 0.0f) return;
        matrices.popPose();
    }
}
