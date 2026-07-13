package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ShieldRendererMixin {

    @Inject(method = "renderItem", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$shieldOffset(
            LivingEntity entity,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            PoseStack matrices,
            SubmitNodeCollector collector,
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
