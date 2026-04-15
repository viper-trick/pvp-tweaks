package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Represents a model with a head.
 */
@Environment(EnvType.CLIENT)
public interface ModelWithHead {
	/**
	 * Gets the head model part.
	 * 
	 * @return the head
	 */
	ModelPart getHead();

	default void applyTransform(MatrixStack matrices) {
		this.getHead().applyTransform(matrices);
	}
}
