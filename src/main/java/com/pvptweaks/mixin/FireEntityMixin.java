package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class FireEntityMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$controlEntityFire(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            Entity entity, CallbackInfo ci) {

        if (PvpTweaksConfig.get().getFireEntityScale() <= 0.0f) {
            ci.cancel();
        }
    }
}
