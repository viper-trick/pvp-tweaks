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

@Mixin(ItemInHandRenderer.class)
public class ShieldRendererMixin {

    @Shadow private ItemStack offHandItem;

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void pvptweaks$sampleShieldPreRender(
            float tickDelta,
            com.mojang.blaze3d.vertex.PoseStack matrices,
            net.minecraft.client.renderer.SubmitNodeCollector collector,
            net.minecraft.client.player.LocalPlayer player,
            int light,
            CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && PvpTweaksConfig.adjusterOpen) {
            if (offHandItem == null || offHandItem.isEmpty()) {
                offHandItem = ShieldSampleStack.INSTANCE;
            }
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void pvptweaks$shieldOffset(
            net.minecraft.world.entity.LivingEntity entity,
            ItemStack itemStack,
            net.minecraft.world.item.ItemDisplayContext displayContext,
            com.mojang.blaze3d.vertex.PoseStack matrices,
            net.minecraft.client.renderer.SubmitNodeCollector collector,
            int light,
            CallbackInfo ci) {

        if (matrices == null || itemStack == null || itemStack.isEmpty()) return;
        if (itemStack.getItem() != Items.SHIELD) return;

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
