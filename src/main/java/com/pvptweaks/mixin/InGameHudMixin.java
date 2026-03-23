package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameOverlayRenderer.class)
public class InGameHudMixin {

    @Inject(method = "method_70938", remap = false,
            at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$cancelTotemAnim(CallbackInfo ci) {
        if (PvpTweaksConfig.get().getTotemPopAnimScale() <= 0.0f)
            ci.cancel();
    }

    // method_70939 = renderItemActivationAnimation
    // scale = MatrixStack.scale(FFF)V  <- Yarn name confirmed
    // method_70939 uses remap=false, but @At target uses Yarn name "scale" (remap=true by default)
    @ModifyArgs(
        method  = "method_70939",
        remap   = false,
        at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"
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
