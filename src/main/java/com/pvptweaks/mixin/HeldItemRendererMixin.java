package com.pvptweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    private static boolean logged = false;

    // method_3233 = renderItem(AbstractClientPlayerEntity, ItemStack, Hand,
    //                          MatrixStack, VertexConsumerProvider, int)
    // remap=false: use intermediary name directly, bypasses Loom name check.
    // @Local captures MatrixStack and ItemStack from method args by type.
    @Inject(method = "method_3233", remap = false, at = @At("HEAD"), require = 0)
    private void pvptweaks$scalePerItem(
            CallbackInfo ci,
            @Local(argsOnly = true) MatrixStack matrices,
            @Local(argsOnly = true) ItemStack item) {

        if (matrices == null || item == null || item.isEmpty()) return;

        if (!logged) {
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] HeldItemMixin ACTIVE on: {}", item.getItem());
            logged = true;
        }

        float scale = PvpTweaksConfig.get().getItemScale(item);
        if (Float.compare(scale, 1.0f) != 0) {
            matrices.scale(scale, scale, scale);
        }
    }
}
