package com.pvptweaks.mixin;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.Sound;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerDebugMixin {
    @Inject(method = "isSoundResourcePresent", at = @At("HEAD"))
    private static void pvptweaks$debugIsSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory, CallbackInfoReturnable<Boolean> cir) {
        if (sound.getLocation().getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] isSoundResourcePresent called for: {} (sound location: {})", id, sound.getLocation());
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] Resource isPresent: {}", resourceFactory.getResource(sound.getLocation()).isPresent());
        }
    }
}

@Mixin(net.minecraft.client.sound.SoundLoader.class)
abstract class SoundLoaderDebugMixin {
    @Inject(method = "loadStatic", at = @At("HEAD"))
    private void pvptweaks$debugLoadStatic(Identifier id, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir) {
        if (id.getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] loadStatic called for: {}", id);
        }
    }
    @Inject(method = "loadStreamed", at = @At("HEAD"))
    private void pvptweaks$debugLoadStreamed(Identifier id, boolean repeatInstantly, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir) {
        if (id.getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] loadStreamed called for: {}", id);
        }
    }
}
