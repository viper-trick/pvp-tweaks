package com.pvptweaks.mixin;

import net.minecraft.client.particle.TotemParticle;
import org.spongepowered.asm.mixin.Mixin;

// Totem particle filtering is handled in ParticleManagerMixin
// via the totem_of_undying particle type ID.
@Mixin(TotemParticle.class)
public class TotemPopMixin {
}
