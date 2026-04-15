package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.MovingBlockRenderState;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PistonBlockEntityRenderState extends BlockEntityRenderState {
	@Nullable
	public MovingBlockRenderState pushedState;
	@Nullable
	public MovingBlockRenderState extendedPistonState;
	public float offsetX;
	public float offsetY;
	public float offsetZ;
}
