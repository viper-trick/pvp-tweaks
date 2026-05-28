package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalEntityRenderer.class)
public class EndCrystalEntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void pvptweaks$scaleEndCrystal(
            net.minecraft.client.render.entity.state.EndCrystalEntityRenderState state,
            MatrixStack matrices,
            net.minecraft.client.render.command.OrderedRenderCommandQueue queue,
            net.minecraft.client.render.state.CameraRenderState cameraState,
            CallbackInfo ci) {

        float scale = PvpTweaksConfig.get().getEndCrystalScale();
        if (matrices != null && Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }}
