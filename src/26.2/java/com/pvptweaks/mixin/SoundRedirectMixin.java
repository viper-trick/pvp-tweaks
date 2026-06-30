package com.pvptweaks.mixin;

import com.pvptweaks.ExplosionTracker;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSoundInstance.class, priority = 500)
public abstract class SoundRedirectMixin {

    @Shadow @Mutable protected Sound sound;
    @Shadow @Mutable protected float volume;
    @Shadow @Mutable protected float pitch;

    @Unique
    private SoundProfile pvptweaks$matchedProfile;

    @Inject(method = "getSoundSet", at = @At("HEAD"))
    private void pvptweaks$identifyProfile(SoundManager manager, CallbackInfoReturnable<WeighedSoundEvents> cir) {
        SoundInstance self = (SoundInstance)(Object) this;
        Identifier current = self.getIdentifier();
        if (current == null) return;
        String path = current.getPath();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        if (path.contains("explode") || path.contains("explosion")) {
            double x = self.getX(), y = self.getY(), z = self.getZ();
            if      (ExplosionTracker.isNearCrystal(x, y, z)) pvptweaks$matchedProfile = cfg.soundCrystal;
            else if (ExplosionTracker.isNearAnchor(x, y, z))  pvptweaks$matchedProfile = cfg.soundAnchor;
            else {
                ExplosionTracker.OtherType type = ExplosionTracker.getOtherType(x, y, z);
                if (type == null) {
                    pvptweaks$matchedProfile = cfg.soundExplosion;
                } else {
                pvptweaks$matchedProfile = switch (type) {
                    case TNT         -> cfg.soundTnt;
                    case CREEPER     -> cfg.soundCreeper;
                    case BED         -> cfg.soundBed;
                    case GHAST       -> cfg.soundGhast;
                    case WIND_CHARGE -> cfg.soundWindCharge;
                    default          -> cfg.soundExplosion;
                };
                }
            }
        } else if (path.contains("totem"))                          pvptweaks$matchedProfile = cfg.soundTotem;
        else if (path.contains("hurt") || path.contains("damage"))  pvptweaks$matchedProfile = cfg.soundHit;
        else if (path.contains("shield") && path.contains("break")) pvptweaks$matchedProfile = cfg.soundShieldBreak;

        // Fallback: check user-added extra sounds by full ID
        if (pvptweaks$matchedProfile == null) {
            pvptweaks$matchedProfile = cfg.extraSounds.get(current.toString());
        }

        // Register redirect BEFORE getSoundSet() calls manager.get(id) so
        // SoundManagerGetRedirect picks it up on the very first call.
        // When profile is null/default we still keep pvptweaks$matchedProfile set
        // so overrideVolume/overridePitch can apply the slider value on the original sound.
        if (pvptweaks$matchedProfile == null || pvptweaks$matchedProfile.isDefault()) {
            // Clear any stale redirect so the original sound plays when reset to default
            com.pvptweaks.sound.SoundRedirects.remove(current);
        } else {
            Identifier originalId = self.getIdentifier();

            if (pvptweaks$matchedProfile.isCustom()) {
                if (pvptweaks$matchedProfile.customPath == null || pvptweaks$matchedProfile.customPath.isBlank()) return;
                Identifier customId =
                    com.pvptweaks.sound.CustomSoundManager.registerCustomSound(pvptweaks$matchedProfile.customPath);
                if (customId == null) return;
                com.pvptweaks.sound.SoundRedirects.set(originalId, customId);
                return;
            }

            if (!pvptweaks$matchedProfile.isPreset() || pvptweaks$matchedProfile.presetId.isBlank()) return;

            String rawId = pvptweaks$matchedProfile.presetId.contains(":")
                ? pvptweaks$matchedProfile.presetId
                : "minecraft:" + pvptweaks$matchedProfile.presetId;

            Identifier newId = Identifier.tryParse(rawId);
            if (newId == null) return;

            if ("pvptweaks".equals(newId.getNamespace())) {
                com.pvptweaks.sound.CustomSoundManager.injectIfMissing(newId);
            }

            com.pvptweaks.sound.SoundRedirects.set(originalId, newId);
        }
    }

    @Inject(method = "getSoundSet", at = @At("RETURN"))
    private void pvptweaks$redirectSoundSet(SoundManager manager,
            CallbackInfoReturnable<WeighedSoundEvents> cir) {

        if (pvptweaks$matchedProfile == null || pvptweaks$matchedProfile.isDefault()) return;

        // Apply pitch/volume overrides if they are set (not default 100)
        if (pvptweaks$matchedProfile.pitchPct != 100) {
            this.pitch = pvptweaks$matchedProfile.pitchPct / 100.0f;
        }
        if (pvptweaks$matchedProfile.volumePct != 100) {
            this.volume = pvptweaks$matchedProfile.volumePct / 100.0f;
        }

        Identifier originalId = ((SoundInstance)(Object) this).getIdentifier();

        if (pvptweaks$matchedProfile.isCustom()) {
            Identifier customId = com.pvptweaks.sound.SoundRedirects.get(originalId);
            if (customId == null) return;
            WeighedSoundEvents customSet = manager.getSoundEvent(customId);
            if (customSet == null) return;
            Sound s = customSet.getSound(net.minecraft.util.RandomSource.create());
            if (s != null) this.sound = s;
            return;
        }

        if (!pvptweaks$matchedProfile.isPreset() || pvptweaks$matchedProfile.presetId.isBlank()) return;

        Identifier newId = com.pvptweaks.sound.SoundRedirects.get(originalId);
        if (newId == null) return;

        WeighedSoundEvents newSet = manager.getSoundEvent(newId);
        if (newSet == null) return;

        Sound newSound = newSet.getSound(net.minecraft.util.RandomSource.create());
        if (newSound != null) this.sound = newSound;
    }

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$overrideVolume(CallbackInfoReturnable<Float> cir) {
        if (pvptweaks$matchedProfile != null && pvptweaks$matchedProfile.volumePct != 100) {
            cir.setReturnValue(pvptweaks$matchedProfile.volumePct / 100.0f);
        }
    }

    @Inject(method = "getPitch", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$overridePitch(CallbackInfoReturnable<Float> cir) {
        if (pvptweaks$matchedProfile != null && pvptweaks$matchedProfile.pitchPct != 100) {
            cir.setReturnValue(pvptweaks$matchedProfile.pitchPct / 100.0f);
        }
    }
}
