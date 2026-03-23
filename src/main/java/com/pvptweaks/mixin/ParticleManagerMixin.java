package com.pvptweaks.mixin;

import com.pvptweaks.ExplosionTracker;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashSet;
import java.util.Set;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    private static final Set<String> seen = new HashSet<>();

    @Inject(
        method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
        at = @At("HEAD"), cancellable = true
    )
    private <T extends ParticleEffect> void pvptweaks$filter(
            T params, double x, double y, double z,
            double vx, double vy, double vz,
            CallbackInfoReturnable<Particle> cir) {

        Identifier pid = Registries.PARTICLE_TYPE.getId(params.getType());
        if (pid == null) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        String p = pid.getPath();

        if (seen.add(p)) PvpTweaksMod.LOGGER.info("[PVP Tweaks] Particle seen: {}", p);

        // Other Explosions: TNT/Creeper/Bed ONLY
        // explosion+poof are shared with crystal/anchor but we filter here
        // crystal and anchor use their OWN sliders below
        if (p.equals("explosion") || p.equals("explosion_emitter")
                || p.equals("poof") || p.equals("large_smoke") || p.equals("smoke")) {
            float r;
            if (ExplosionTracker.isNearAnchor(x, y, z)) {
                r = cfg.getAnchorExplosionRatio();
            } else if (ExplosionTracker.isNearCrystal(x, y, z)) {
                r = cfg.getEnderExplosionRatio();
            } else {
                r = cfg.getExplosionRatio();
            }
            if (r <= 0f || (r < 1f && Math.random() > r)) cir.setReturnValue(null);
            return;
        }

        // Ender flash – crystal pop
        if (p.equals("flash") || p.contains("dragon")) {
            float r = cfg.getEnderExplosionRatio();
            if (r <= 0f || (r < 1f && Math.random() > r)) cir.setReturnValue(null);
            return;
        }

        // Crystal ambient
        if (p.equals("end_rod")) {
            float r = cfg.getCrystalRatio();
            if (r <= 0f || (r < 1f && Math.random() > r)) cir.setReturnValue(null);
            return;
        }

        // Totem
        if (p.equals("totem_of_undying")) {
            float r = cfg.getTotemPopScale();
            if (r <= 0f || (r < 1f && Math.random() > r)) cir.setReturnValue(null);
            return;
        }

        // Fire on ground/entities
        if (p.equals("flame") || p.equals("small_flame")
                || p.equals("soul_fire_flame") || p.equals("copper_fire_flame")) {
            float r = cfg.getFireEntityScale();
            if (r <= 0f || (r < 1f && Math.random() > r)) cir.setReturnValue(null);
            return;
        }

        // Hit/Crit
        if (p.equals("crit") || p.equals("enchanted_hit") || p.equals("damage_indicator")) {
            if (!cfg.showHitParticles) cir.setReturnValue(null);
        }
    }
}
