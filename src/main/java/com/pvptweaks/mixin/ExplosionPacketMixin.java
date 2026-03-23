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
        if (radius >= 5.5f) {
            ExplosionTracker.recordCrystal(c.x, c.y, c.z);
        } else if (radius >= 4.5f) {
            ExplosionTracker.recordAnchor(c.x, c.y, c.z);
        }
    }
}