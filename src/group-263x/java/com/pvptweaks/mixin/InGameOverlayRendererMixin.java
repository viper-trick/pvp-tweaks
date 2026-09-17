package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderItemActivationAnimation", remap = false,
            at = @At("HEAD"), cancellable = true)
    private void pvptweaks$cancelTotemAnim(CallbackInfo ci) {
        if (PvpTweaksConfig.get().getTotemPopAnimScale() <= 0.0f)
            ci.cancel();
    }

    @ModifyArgs(
            method = "renderItemActivationAnimation",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
            )
    )
    private static void pvptweaks$scaleTotemAnim(Args args) {
        float s = PvpTweaksConfig.get().getTotemPopAnimScale();
        if (s > 0.0f && Float.compare(s, 1.0f) != 0) {
            args.set(0, (float) args.get(0) * s);
            args.set(1, (float) args.get(1) * s);
            args.set(2, (float) args.get(2) * s);
        }
    }
}
