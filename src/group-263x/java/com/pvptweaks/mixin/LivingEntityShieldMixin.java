package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.ShieldSampleStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityShieldMixin {

    private static boolean shouldFakeActive() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        return cfg.shieldSampleShield && cfg.shieldSampleActive && PvpTweaksConfig.adjusterOpen;
    }

    @Inject(method = "getOffhandItem", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$sampleShield(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack current = cir.getReturnValue();
        if (current != null && !current.isEmpty()) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && PvpTweaksConfig.adjusterOpen) {
            cir.setReturnValue(ShieldSampleStack.INSTANCE);
        }
    }

    @Inject(method = "getUseItem", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$sampleUseItem(CallbackInfoReturnable<ItemStack> cir) {
        if (shouldFakeActive()) {
            cir.setReturnValue(ShieldSampleStack.INSTANCE);
        }
    }

    @Inject(method = "getUseItemRemainingTicks", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$sampleUseTicks(CallbackInfoReturnable<Integer> cir) {
        if (shouldFakeActive()) {
            cir.setReturnValue(100);
        }
    }
}
