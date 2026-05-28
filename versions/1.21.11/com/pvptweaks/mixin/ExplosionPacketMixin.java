package com.pvptweaks.mixin;

import com.pvptweaks.ExplosionTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ExplosionPacketMixin {

    @Inject(method = "onExplosion", at = @At("HEAD"))
    private void pvptweaks$trackExplosionSource(ExplosionS2CPacket packet, CallbackInfo ci) {
        Vec3d c = packet.center();
        float radius = packet.radius();

        // Crystal (radius ≥ 5.5) and Anchor (radius ≥ 4.5) — existing logic
        if (radius >= 5.5f) {
            ExplosionTracker.recordCrystal(c.x, c.y, c.z);
            return;
        }
        if (radius >= 4.5f) {
            ExplosionTracker.recordAnchor(c.x, c.y, c.z);
            return;
        }

        // Classify "Other Explosions" by radius heuristics:
        //   Wind Charge ≈ 1.5  (small, fast)
        //   Ghast       ≈ 1.0
        //   Creeper     ≈ 3.0
        //   TNT         ≈ 4.0
        //   Bed         ≈ 5.0  (in Nether/End)
        //
        // These are approximate. Servers may tweak them; we classify generously.
        ExplosionTracker.OtherType type;
        if (radius <= 1.6f) {
            type = ExplosionTracker.OtherType.WIND_CHARGE;
        } else if (radius <= 1.1f) {
            type = ExplosionTracker.OtherType.GHAST;
        } else if (radius <= 3.5f) {
            type = ExplosionTracker.OtherType.CREEPER;
        } else if (radius <= 4.2f) {
            type = ExplosionTracker.OtherType.TNT;
        } else {
            type = ExplosionTracker.OtherType.BED;
        }
        ExplosionTracker.recordOther(c.x, c.y, c.z, type);
    }
}