package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void pvptweaks$scalePerItem(
            CallbackInfo ci,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true) PoseStack matrices) {

        if (matrices == null || stack == null || stack.isEmpty()) return;

        float scale = PvpTweaksConfig.get().getItemScale(stack);
        if (Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }
}
