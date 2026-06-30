package com.pvptweaks.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerDebugMixin {
    @Inject(method = "validateSoundResource", at = @At("HEAD"))
    private static void pvptweaks$debugIsSoundResourcePresent(Sound sound, ResourceLocation id, ResourceProvider resourceFactory, CallbackInfoReturnable<Boolean> cir) {
        if (sound.getPath().getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] isSoundResourcePresent called for: {} (sound location: {})", id, sound.getPath());
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] Resource isPresent: {}", resourceFactory.getResource(sound.getPath()).isPresent());
        }
    }
}

@Mixin(net.minecraft.client.sounds.SoundBufferLibrary.class)
abstract class SoundLoaderDebugMixin {
    @Inject(method = "getCompleteBuffer", at = @At("HEAD"))
    private void pvptweaks$debugLoadStatic(ResourceLocation id, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir) {
        if (id.getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] loadStatic called for: {}", id);
        }
    }
    @Inject(method = "getStream", at = @At("HEAD"))
    private void pvptweaks$debugLoadStreamed(ResourceLocation id, boolean repeatInstantly, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir) {
        if (id.getNamespace().equals("pvptweaks")) {
            com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] loadStreamed called for: {}", id);
        }
    }
}
