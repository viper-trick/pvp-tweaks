package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderState extends BlockEntityRenderState {
	@Nullable
	public Direction shakeDirection;
	public float ringTicks;
}
