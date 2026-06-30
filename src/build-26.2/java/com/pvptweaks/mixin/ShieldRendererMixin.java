package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ItemInHandRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(ItemInHandRenderer.class)
public class ShieldRendererMixin {

    @Inject(method = "method_3233", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$shieldOffset(
            CallbackInfo ci,
            @Local(argsOnly = true) PoseStack matrices,
            @Local(argsOnly = true) ItemStack item) {

        if (matrices == null || item == null || item.isEmpty()) return;
        if (item.getItem() != Items.SHIELD) return;

        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        float ox = cfg.shieldOffsetX / 100f;
        float oy = cfg.shieldOffsetY / 100f;
        float oz = cfg.shieldOffsetZ / 100f;
        if (ox != 0 || oy != 0 || oz != 0) {
            matrices.translate(ox, oy, oz);
        }
        if (cfg.shieldRotX != 0) {
            matrices.mulPose(new Quaternionf().rotationX(cfg.shieldRotX * (float)Math.PI / 180f));
        }
        if (cfg.shieldRotY != 0) {
            matrices.mulPose(new Quaternionf().rotationY(cfg.shieldRotY * (float)Math.PI / 180f));
        }
        if (cfg.shieldRotZ != 0) {
            matrices.mulPose(new Quaternionf().rotationZ(cfg.shieldRotZ * (float)Math.PI / 180f));
        }
    }
}
