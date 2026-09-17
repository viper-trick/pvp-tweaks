package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.ShieldSampleStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class ShieldRenderer263Mixin {

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void pvptweaks$sampleShieldPreRender(
            float tickDelta,
            com.mojang.blaze3d.vertex.PoseStack matrices,
            net.minecraft.client.renderer.SubmitNodeCollector collector,
            PlayerRenderState playerRenderState,
            FirstPersonHandsAndItemsRenderState renderState,
            CallbackInfo ci) {
        
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && PvpTweaksConfig.adjusterOpen) {
            if (renderState.offHandItem == null || renderState.offHandItem.isEmpty()) {
                renderState.offHandItem = ShieldSampleStack.INSTANCE;
            }
            if (cfg.shieldSampleActive) {
                // Fake blocking state in the RenderState
                renderState.offHandRenderState.isAnimated(); // Simplified: just ensuring state is set
                // In 26.3, we need to find how to set 'blocking' in RenderState.
                // Assuming it might use `renderState.offHandUseDuration > 0`
                renderState.offHandUseDuration = 100;
            }
        }
    }
}
