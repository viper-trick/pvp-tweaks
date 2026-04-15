package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Represents a model with a hat.
 */
@Environment(EnvType.CLIENT)
public interface ModelWithHat<T extends EntityRenderState> {
	void rotateArms(T state, MatrixStack matrices);
}
