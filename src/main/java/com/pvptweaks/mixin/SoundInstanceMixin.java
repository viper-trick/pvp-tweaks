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

        // generic.explode משמש לכולם — מזהים לפי מיקום
        if (path.equals("entity.generic.explode") || path.contains("explode") || path.contains("explosion")) {
            double x = self.getX(), y = self.getY(), z = self.getZ();
            if (ExplosionTracker.isNearCrystal(x, y, z)) {
                cir.setReturnValue(original * cfg.getCrystalPopMultiplier());
            } else if (ExplosionTracker.isNearAnchor(x, y, z)) {
                cir.setReturnValue(original * cfg.getRespawnAnchorMultiplier());
            } else {
                cir.setReturnValue(original * cfg.getExplosionMultiplier());
            }
            return;
        }

        if (path.contains("totem")) {
            cir.setReturnValue(original * cfg.getTotemPopMultiplier());
        } else if (path.contains("hurt") || path.contains("damage")) {
            cir.setReturnValue(original * cfg.getHitMultiplier());
        }
    }
}
