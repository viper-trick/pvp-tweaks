package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBoxRendering;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderState extends BlockEntityRenderState {
	public boolean visible;
	public StructureBoxRendering.RenderMode renderMode;
	public StructureBoxRendering.StructureBox structureBox;
	@Nullable
	public StructureBlockBlockEntityRenderState.InvisibleRenderType[] invisibleBlocks;
	@Nullable
	public boolean[] field_62682;

	@Environment(EnvType.CLIENT)
	public static enum InvisibleRenderType {
		AIR,
		BARRIER,
		LIGHT,
		STRUCTURE_VOID;
	}
}
