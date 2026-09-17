package com.pvptweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.3: ItemInHandRenderer was replaced by a render-state based pipeline.
 * FirstPersonHandsAndItemsRenderer.submitArmWithItem is the method that submits a
 * single held item, so per-item scale and the shield offset/rotation tweaks are
 * applied to the PoseStack it receives.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void pvptweaks$scaleAndOffsetPerItem(
            CallbackInfo ci,
            @Local(argsOnly = true) PoseStack matrices,
            @Local(argsOnly = true) ItemStack itemStack) {

        if (matrices == null || itemStack == null || itemStack.isEmpty()) return;

        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        if (itemStack.getItem() == Items.SHIELD) {
            float ox = cfg.shieldOffsetX / 100f;
            float oy = cfg.shieldOffsetY / 100f;
            float oz = cfg.shieldOffsetZ / 100f;
            if (ox != 0 || oy != 0 || oz != 0) {
                matrices.translate(ox, oy, oz);
            }
            if (cfg.shieldRotX != 0) {
                matrices.rotateDegrees(Axis.XP, cfg.shieldRotX);
            }
            if (cfg.shieldRotY != 0) {
                matrices.rotateDegrees(Axis.YP, cfg.shieldRotY);
            }
            if (cfg.shieldRotZ != 0) {
                matrices.rotateDegrees(Axis.ZP, cfg.shieldRotZ);
            }
        }

        float scale = cfg.getItemScale(itemStack);
        if (Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }
}

