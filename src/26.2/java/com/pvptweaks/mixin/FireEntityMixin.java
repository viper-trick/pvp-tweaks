package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FlameFeatureRenderer.class)
public class FireEntityMixin {

    @Inject(method = "buildGroup", at = @At("RETURN"))
    private void pvptweaks$scaleFire(FeatureFrameContext context, List<FlameFeatureRenderer.Submit> submits, CallbackInfo ci) {
        float scale = PvpTweaksConfig.get().getFireEntityScale();
        if (scale != 1.0f) {
            submits.replaceAll(submit -> {
                PoseStack.Pose scaledPose = submit.pose().copy();
                scaledPose.scale(scale, scale, scale);
                return new FlameFeatureRenderer.Submit(scaledPose, submit.entityRenderState(), submit.rotation());
            });
        }
    }
}
