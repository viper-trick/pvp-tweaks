package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables the carved-pumpkin overlay ("pumpkin blur") when the option is enabled.
 *
 * In 26.1, the pumpkin blur is rendered via ScreenEffectRenderer.renderScreenEffect.
 * The first boolean parameter controls the pumpkin overlay. We cancel when
 * disablePumpkinBlur is enabled and the pumpkin overlay would be drawn.
 */
@Mixin(ScreenEffectRenderer.class)
public class PumpkinBlurMixin {

    @Inject(
        method = "renderScreenEffect",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void pvptweaks$onRenderScreenEffect(boolean pumpkin, boolean flag, float partialTick,
            net.minecraft.client.renderer.SubmitNodeCollector submitNodes, boolean flag2, CallbackInfo ci) {
        if (PvpTweaksConfig.get().disablePumpkinBlur && pumpkin) {
            ci.cancel();
        }
    }
}
