package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Defines rendering setup and draw logic for particles based on their requirements for depth checking, textures, and transparency.
 * 
 * <p>
 * Each {@link Particle} returns a sheet in {@link Particle#getType()}.
 * When particles are rendered, each sheet will be drawn once.
 */
@Environment(EnvType.CLIENT)
public record ParticleTextureSheet(String name) {
	public static final ParticleTextureSheet SINGLE_QUADS = new ParticleTextureSheet("SINGLE_QUADS");
	public static final ParticleTextureSheet ITEM_PICKUP = new ParticleTextureSheet("ITEM_PICKUP");
	public static final ParticleTextureSheet ELDER_GUARDIANS = new ParticleTextureSheet("ELDER_GUARDIANS");
	public static final ParticleTextureSheet NO_RENDER = new ParticleTextureSheet("NO_RENDER");
}
