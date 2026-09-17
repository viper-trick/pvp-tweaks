package com.pvptweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.ShieldSampleStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.3: first-person hand data now lives in a render state that is extracted once per
 * frame by FirstPersonHandsAndItems.extractRenderState and later consumed by
 * FirstPersonHandsAndItemsRenderer. To preview a shield while the adjuster screen is
 * open, seed the off-hand item, rebuild its item model state and force both hands to
 * render.
 */
@Mixin(FirstPersonHandsAndItems.class)
public class ShieldRendererMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void pvptweaks$sampleShieldPreview(
            CallbackInfo ci,
            @Local(argsOnly = true) FirstPersonHandsAndItemsRenderState renderState) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.shieldSampleShield || !PvpTweaksConfig.adjusterOpen) return;
        if (renderState.offHandItem != null && !renderState.offHandItem.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        renderState.offHandItem = ShieldSampleStack.INSTANCE;
        renderState.handRenderSelection =
                FirstPersonHandsAndItemsRenderState.HandRenderSelection.RENDER_BOTH_HANDS;
        renderState.offHandUseDuration = cfg.shieldSampleActive ? 100 : 0;

        ItemDisplayContext context = player.getMainArm() == HumanoidArm.RIGHT
                ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

        renderState.offHandRenderState.clear();
        minecraft.getItemModelResolver().updateForTopItem(
                renderState.offHandRenderState,
                ShieldSampleStack.INSTANCE,
                context,
                player.level(),
                player,
                player.getId() + context.ordinal());
    }
}

