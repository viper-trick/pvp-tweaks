package com.pvptweaks.mixin;

import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.Identifier;

@Mixin(SoundManager.class)
public class SoundManagerDebugMixin {
    @Inject(method = "getSoundEvent", at = @At("HEAD"))
    private void pvptweaks$debugGetSoundEvent(Identifier id, CallbackInfoReturnable cir) {
        if (id.getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] getSoundEvent called for: {}", id);
        }
    }
}
