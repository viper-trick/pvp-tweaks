package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class FireOverlayMixin {

    @Inject(method = "submitFire", at = @At("HEAD"), cancellable = true)
    private static void pvptweaks$fireHead(PoseStack poseStack, SubmitNodeCollector submitNodes, TextureAtlasSprite sprite, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float scale = cfg.getFireOverlayScale();
        if (scale <= 0.0f) {
            ci.cancel();
            return;
        }
        if (scale != 1.0f) {
            poseStack.pushPose();
            float translation = (1.0f - scale) * -0.5f;
            poseStack.translate(0.0, translation, 0.0);
            poseStack.scale(scale, scale, 1.0f);
        }
    }

    @Inject(method = "submitFire", at = @At("RETURN"))
    private static void pvptweaks$fireTail(PoseStack poseStack, SubmitNodeCollector submitNodes, TextureAtlasSprite sprite, CallbackInfo ci) {
        float scale = PvpTweaksConfig.get().getFireOverlayScale();
        if (scale != 1.0f) {
            poseStack.popPose();
        }
    }
}
