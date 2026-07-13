package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public class FireHeightMixin {

    @Inject(method = "submitFlame", remap = false, at = @At("HEAD"), cancellable = true)
    private void pvptweaks$cancelFlame(PoseStack poseStack, EntityRenderState state, Quaternionf rotation, CallbackInfo ci) {
        System.out.println("[pvptweaks] SubmitNodeCollection.submitFlame() called - CANCELLING FOR TEST");
        ci.cancel();
    }
}
