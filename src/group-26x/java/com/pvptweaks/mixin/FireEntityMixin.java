package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In 26.1, FlameFeatureRenderer.renderSolid(SubmitNodeCollection, MultiBufferSource$BufferSource, AtlasManager)
 * replaces the old FireCommandRenderer.method_73005(MatrixStack.Entry, ...).
 * The new API has no PoseStack to scale — fire entity scaling is not directly portable.
 */
@Mixin(FlameFeatureRenderer.class)
public class FireEntityMixin {

    @Inject(method = "renderSolid", at = @At("HEAD"))
    private void pvptweaks$scaleFire(
            net.minecraft.client.renderer.SubmitNodeCollection submitNodes,
            net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,
            AtlasManager atlasManager,
            CallbackInfo ci) {

        float scale = PvpTweaksConfig.get().getFireEntityScale();
        // renderSolid has no PoseStack to scale (unlike the old Yarn FireCommandRenderer).
        // Scaling via SubmitNodeCollection/BufferSource is non-trivial.
    }
}
