package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class FireOverlayMixin {

    @Inject(method = "renderScreenEffect", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$onRenderScreenEffect(boolean living, boolean fire, float alpha, net.minecraft.client.renderer.SubmitNodeCollector submitNodes, boolean inWater, CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        if (fire && "none".equals(cfg.firePreset)) {
            ci.cancel();
            return;
        }

        // Note: fire overlay height scaling is not directly portable to 26.1.
        // The old Yarn InGameOverlayRenderer.renderFireOverlay(MatrixStack, ...) exposed the
        // matrix stack for scaling. ScreenEffectRenderer.renderScreenEffect wraps this
        // internally without exposing the PoseStack.
    }
}
