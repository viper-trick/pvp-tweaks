package com.pvptweaks.mixin;

import com.pvptweaks.ExplosionTracker;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public class SoundInstanceMixin {

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$modifyVolume(CallbackInfoReturnable<Float> cir) {
        SoundInstance self = (SoundInstance)(Object) this;
        Identifier id = self.getId();
        if (id == null) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float original = cir.getReturnValue();
        String path = id.getPath();

        // entity.generic.explode — identify by position what kind of explosion this is
        if (path.equals("entity.generic.explode") || path.contains("explode") || path.contains("explosion")) {
            double x = self.getX(), y = self.getY(), z = self.getZ();
            if (ExplosionTracker.isNearCrystal(x, y, z)) {
                cir.setReturnValue(original * cfg.getCrystalPopMultiplier());
            } else if (ExplosionTracker.isNearAnchor(x, y, z)) {
                cir.setReturnValue(original * cfg.getRespawnAnchorMultiplier());
            } else {
                // Per-type "Other Explosions" volume
                ExplosionTracker.OtherType type = ExplosionTracker.getOtherType(x, y, z);
                float mult = otherTypeVolumeMultiplier(cfg, type);
                cir.setReturnValue(original * mult);
            }
            return;
        }

        if (path.contains("totem")) {
            cir.setReturnValue(original * cfg.getTotemPopMultiplier());
        } else if (path.contains("hurt") || path.contains("damage")) {
            cir.setReturnValue(original * cfg.getHitMultiplier());
        }
    }

    private static float otherTypeVolumeMultiplier(PvpTweaksConfig cfg,
                                                    ExplosionTracker.OtherType type) {
        if (type == null) return cfg.getExplosionMultiplier();
        return switch (type) {
            case TNT         -> cfg.getTntExplosionMultiplier();
            case CREEPER     -> cfg.getCreeperExplosionMultiplier();
            case BED         -> cfg.getBedExplosionMultiplier();
            case GHAST       -> cfg.getGhastExplosionMultiplier();
            case WIND_CHARGE -> cfg.getWindChargeMultiplier();
            default          -> cfg.getExplosionMultiplier();
        };
    }
}
