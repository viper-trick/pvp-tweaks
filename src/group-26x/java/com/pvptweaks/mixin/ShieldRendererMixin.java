package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ItemInHandRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.player.AbstractClientPlayer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In 26.1, shield rendering is done in ItemInHandRenderer.renderArmWithItem.
 * We inject at HEAD to apply shield offset/rotation transforms.
 */
@Mixin(ItemInHandRenderer.class)
public class ShieldRendererMixin {

    @Inject(method = "renderArmWithItem", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$shieldOffset(
            AbstractClientPlayer player, float armPitch, float pitch, InteractionHand hand,
            float swingProgress, ItemStack item, float equipProgress, PoseStack matrices,
            net.minecraft.client.renderer.SubmitNodeCollector submitNodes, int light,
            CallbackInfo ci) {

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
