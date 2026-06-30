package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalEntityRendererMixin {

    @Inject(method = "submit", at = @At("HEAD"))
    private void pvptweaks$scaleEndCrystal(
            net.minecraft.client.renderer.entity.state.EndCrystalRenderState state,
            PoseStack matrices,
            net.minecraft.client.renderer.SubmitNodeCollector queue,
            net.minecraft.client.renderer.state.CameraRenderState cameraState,
            CallbackInfo ci) {

        float scale = PvpTweaksConfig.get().getEndCrystalScale();
        if (matrices != null && Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }}
