package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$cancelTotemAnim(PoseStack poseStack, float alpha, net.minecraft.client.renderer.SubmitNodeCollector submitNodes, CallbackInfo ci) {
        if (PvpTweaksConfig.get().getTotemPopAnimScale() <= 0.0f)
            ci.cancel();
    }

    @ModifyArgs(
        method = "renderItemActivationAnimation",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
        ),
        require = 0
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
