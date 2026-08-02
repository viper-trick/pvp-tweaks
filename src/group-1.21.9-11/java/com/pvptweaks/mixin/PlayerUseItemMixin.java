package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class PlayerUseItemMixin {

    @Inject(method = "isUsingItem", remap = true, at = @At("RETURN"), cancellable = true, require = 1)
    private void pvptweaks$sampleActive(CallbackInfoReturnable<Boolean> cir) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && cfg.shieldSampleActive && PvpTweaksConfig.adjusterOpen) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getUsedItemHand", remap = true, at = @At("RETURN"), cancellable = true, require = 1)
    private void pvptweaks$sampleActiveHand(CallbackInfoReturnable<InteractionHand> cir) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && cfg.shieldSampleActive && PvpTweaksConfig.adjusterOpen) {
            cir.setReturnValue(InteractionHand.OFF_HAND);
        }
    }

}