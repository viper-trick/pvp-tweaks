package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(HeldItemRenderer.class)
public class ShieldRendererMixin {

    @Inject(method = "method_3233", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$shieldOffset(
            CallbackInfo ci,
            @Local(argsOnly = true) MatrixStack matrices,
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
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(cfg.shieldRotX));
        }
        if (cfg.shieldRotY != 0) {
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(cfg.shieldRotY));
        }
        if (cfg.shieldRotZ != 0) {
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(cfg.shieldRotZ));
        }
    }
}
