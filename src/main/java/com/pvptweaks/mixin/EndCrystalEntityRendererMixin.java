package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(EndCrystalEntityRenderer.class)
public class EndCrystalEntityRendererMixin {

    @Inject(
        method = "method_3908",
        remap  = false,
        at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
            remap  = true,
            shift  = At.Shift.AFTER,
            ordinal = 0
        ),
        require = 0
    )
    private void pvptweaks$scaleEndCrystal(
            CallbackInfo ci,
            @Local(argsOnly = true) MatrixStack matrices) {

        float scale = PvpTweaksConfig.get().getEndCrystalScale();
        if (matrices != null && Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }
}
