package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(FlameFeatureRenderer.class)
public class FireEntityMixin {

    @Inject(method = "prepare", remap = false, at = @At("HEAD"), cancellable = true)
    private void pvptweaks$cancelPrepare(
            FlameFeatureRenderer.Submit submit,
            VertexConsumer buffer,
            TextureAtlasSprite fire0,
            TextureAtlasSprite fire1,
            CallbackInfo ci
    ) {
        if (PvpTweaksConfig.get().getFireEntityScale() == 0.0f) {
            ci.cancel();
        }
    }
}
