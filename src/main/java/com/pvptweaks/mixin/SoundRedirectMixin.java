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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public class SoundRedirectMixin {

    @Shadow @Mutable protected Sound sound;

    @Inject(method = "getSoundSet", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$redirectSoundSet(SoundManager manager,
            CallbackInfoReturnable<WeightedSoundSet> cir) {

        SoundInstance self = (SoundInstance)(Object) this;
        Identifier current = self.getId();
        if (current == null) return;
        String path = current.getPath();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        SoundProfile profile = null;
        if (path.contains("explode") || path.contains("explosion")) {
            double x = self.getX(), y = self.getY(), z = self.getZ();
            if      (ExplosionTracker.isNearCrystal(x, y, z)) profile = cfg.soundCrystal;
            else if (ExplosionTracker.isNearAnchor(x, y, z))  profile = cfg.soundAnchor;
            else {
                ExplosionTracker.OtherType type = ExplosionTracker.getOtherType(x, y, z);
                profile = switch (type) {
                    case TNT         -> cfg.soundTnt;
                    case CREEPER     -> cfg.soundCreeper;
                    case BED         -> cfg.soundBed;
                    case GHAST       -> cfg.soundGhast;
                    case WIND_CHARGE -> cfg.soundWindCharge;
                    default          -> cfg.soundExplosion;
                };
            }
        } else if (path.contains("totem"))                          profile = cfg.soundTotem;
        else if (path.contains("hurt") || path.contains("damage"))  profile = cfg.soundHit;
        else if (path.contains("shield") && path.contains("break")) profile = cfg.soundShieldBreak;

        if (profile == null || profile.isDefault()) return;

        // Custom mode: absolute file path
        if (profile.isCustom()) {
            if (profile.customPath == null || profile.customPath.isBlank()) return;
            Identifier customId =
                com.pvptweaks.sound.CustomSoundManager.registerCustomSound(profile.customPath);
            if (customId == null) return;
            WeightedSoundSet customSet = manager.get(customId);
            if (customSet == null) {
                PvpTweaksMod.LOGGER.warn("[PVP Tweaks] custom not in registry, reload scheduled: {}", customId);
                return;
            }
            Sound s = customSet.getSound(net.minecraft.util.math.random.Random.create());
            if (s != null) this.sound = s;
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] custom played: {} -> {}", path, customId);
            cir.setReturnValue(customSet);
            return;
        }

        // Preset mode
        if (!profile.isPreset() || profile.presetId.isBlank()) return;

        // FIX: vanilla MC sounds are stored without namespace (legacy data).
        // "entity.firework_rocket.blast" → Identifier.tryParse() = null → silent skip
        String rawId = profile.presetId.contains(":")
            ? profile.presetId
            : "minecraft:" + profile.presetId;

        Identifier newId = Identifier.tryParse(rawId);
        if (newId == null) {
            PvpTweaksMod.LOGGER.warn("[PVP Tweaks] bad sound id: '{}'", rawId);
            return;
        }

        WeightedSoundSet newSet = manager.get(newId);
        if (newSet == null) {
            PvpTweaksMod.LOGGER.warn("[PVP Tweaks] sound not in SoundManager: {}", newId);
            return;
        }

        Sound newSound = newSet.getSound(net.minecraft.util.math.random.Random.create());
        if (newSound != null) this.sound = newSound;
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] redirected: {} -> {}", path, newId);
        cir.setReturnValue(newSet);
    }
}
