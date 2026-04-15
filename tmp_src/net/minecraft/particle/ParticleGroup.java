package net.minecraft.particle;

/**
 * A group for particles. This group imposes a limit on the numbers of
 * particles from this group rendered in a particle manager. Additional
 * particles will be discarded when attempted to be rendered.
 * 
 * @see net.minecraft.client.particle.Particle#getGroup()
 */
public record ParticleGroup(int maxCount) {
	/**
	 * The group for the {@linkplain net.minecraft.particle.ParticleTypes#SPORE_BLOSSOM_AIR
	 * minecraft:spore_blossom_air} particle type. It has a count limit of 1000.
	 */
	public static final ParticleGroup SPORE_BLOSSOM_AIR = new ParticleGroup(1000);
}
