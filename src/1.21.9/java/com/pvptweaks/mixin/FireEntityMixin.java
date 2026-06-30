package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.resources.model.AtlasManager;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlameFeatureRenderer.class)
public class FireEntityMixin {

    @Inject(method = "method_73005", remap = false, at = @At("HEAD"))
    private void pvptweaks$scaleFire(
            PoseStack.Pose matricesEntry, MultiBufferSource vertexConsumers,
            EntityRenderState renderState, Quaternionf rotation, AtlasManager atlasManager,
            CallbackInfo ci) {
        
        float scale = PvpTweaksConfig.get().getFireEntityScale();
        if (scale != 1.0f) {
            // Apply scale to the matrices entry.
            // FireCommandRenderer uses the entry directly, so we scale it.
            // Note: matricesEntry is the entry being used for vertex calls.
            // Scaling here affects all subsequent vertices in this draw call.
            matricesEntry.scale(scale, scale, scale);
        }
    }
}
