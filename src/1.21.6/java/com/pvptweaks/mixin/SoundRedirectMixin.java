package com.pvptweaks.mixin;

import com.pvptweaks.ExplosionTracker;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class SoundRedirectMixin {

    @Shadow @Mutable protected Sound sound;
    @Shadow @Mutable protected float volume;
    @Shadow @Mutable protected float pitch;

    @Unique
    private SoundProfile pvptweaks$matchedProfile;

    @Inject(method = "getSoundSet", at = @At("HEAD"))
    private void pvptweaks$identifyProfile(SoundManager manager, CallbackInfoReturnable<WeightedSoundSet> cir) {
        SoundInstance self = (SoundInstance)(Object) this;
        Identifier current = self.getId();
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
    }

    @Inject(method = "getSoundSet", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$redirectSoundSet(SoundManager manager,
            CallbackInfoReturnable<WeightedSoundSet> cir) {

        if (pvptweaks$matchedProfile == null || pvptweaks$matchedProfile.isDefault()) return;

        // Apply pitch/volume overrides if they are set (not default 100)
        if (pvptweaks$matchedProfile.pitchPct != 100) {
            this.pitch = pvptweaks$matchedProfile.pitchPct / 100.0f;
        }
        if (pvptweaks$matchedProfile.volumePct != 100) {
            this.volume = pvptweaks$matchedProfile.volumePct / 100.0f;
        }

        // Custom mode: absolute file path
        if (pvptweaks$matchedProfile.isCustom()) {
            if (pvptweaks$matchedProfile.customPath == null || pvptweaks$matchedProfile.customPath.isBlank()) return;
            Identifier customId =
                com.pvptweaks.sound.CustomSoundManager.registerCustomSound(pvptweaks$matchedProfile.customPath);
            if (customId == null) return;
            WeightedSoundSet customSet = manager.get(customId);
            if (customSet == null) return;
            Sound s = customSet.getSound(net.minecraft.util.math.random.Random.create());
            if (s != null) this.sound = s;
            cir.setReturnValue(customSet);
            return;
        }

        // Preset mode
        if (!pvptweaks$matchedProfile.isPreset() || pvptweaks$matchedProfile.presetId.isBlank()) return;

        String rawId = pvptweaks$matchedProfile.presetId.contains(":")
            ? pvptweaks$matchedProfile.presetId
            : "minecraft:" + pvptweaks$matchedProfile.presetId;

        Identifier newId = Identifier.tryParse(rawId);
        if (newId == null) return;

        WeightedSoundSet newSet = manager.get(newId);
        if (newSet == null) return;

        Sound newSound = newSet.getSound(net.minecraft.util.math.random.Random.create());
        if (newSound != null) this.sound = newSound;
        cir.setReturnValue(newSet);
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
