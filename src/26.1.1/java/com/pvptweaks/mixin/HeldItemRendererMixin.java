package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void pvptweaks$scalePerItem(
            net.minecraft.entity.LivingEntity entity, ItemStack stack, ItemDisplayContext mode,
            MatrixStack matrices, net.minecraft.client.render.command.OrderedRenderCommandQueue queue, int light,
            CallbackInfo ci) {

        if (matrices == null || stack == null || stack.isEmpty()) return;

        float scale = PvpTweaksConfig.get().getItemScale(stack);
        if (Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }}
