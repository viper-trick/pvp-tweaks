package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.FireCommandRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireCommandRenderer.class)
public class FireEntityMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void pvptweaks$scaleFire(
            MatrixStack.Entry matricesEntry, VertexConsumerProvider vertexConsumers,
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
