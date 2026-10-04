package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$scaleTotemAnim(
            PoseStack poseStack,
            float partialTick,
            SubmitNodeCollector submitNodeCollector,
            CallbackInfo ci
    ) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg == null) return;

        float scale = cfg.getTotemPopAnimScale();
        if (scale <= 0.0f) {
            ci.cancel();
        } else if (scale != 1.0f) {
            poseStack.scale(scale, scale, scale);
        }
    }
}
