package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.ShieldSampleStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(ItemInHandRenderer.class)
public class ShieldRendererMixin {

    @Shadow private ItemStack offHandItem;

    @Inject(method = "method_22976", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$sampleShieldPreRender(CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && PvpTweaksConfig.adjusterOpen) {
            if (offHandItem == null || offHandItem.isEmpty()) {
                offHandItem = ShieldSampleStack.INSTANCE;
            }
        }
    }

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
            matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(cfg.shieldRotX));
        }
        if (cfg.shieldRotY != 0) {
            matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(cfg.shieldRotY));
        }
        if (cfg.shieldRotZ != 0) {
            matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(cfg.shieldRotZ));
        }
    }
}