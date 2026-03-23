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
            else                                                profile = cfg.soundExplosion;
        } else if (path.contains("totem"))                         profile = cfg.soundTotem;
        else if (path.contains("hurt") || path.contains("damage")) profile = cfg.soundHit;
        else if (path.contains("shield") && path.contains("break")) profile = cfg.soundShieldBreak;

        if (profile == null || profile.isDefault()) return;
        if (!profile.isPreset() || profile.presetId.isBlank()) return;

        Identifier newId = Identifier.tryParse(profile.presetId);
        if (newId == null) return;

        WeightedSoundSet newSet = manager.get(newId);
        if (newSet == null) return;

        // עדכן גם את this.sound — זה מה שה-engine משמיע בפועל
        Sound newSound = newSet.getSound(net.minecraft.util.math.random.Random.create());
        if (newSound != null) {
            this.sound = newSound;
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Sound redirected: {} -> {}", path, newId);
        }
        cir.setReturnValue(newSet);
    }
}
