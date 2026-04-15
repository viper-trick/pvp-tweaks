package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderState;

@Environment(EnvType.CLIENT)
public class ShelfBlockEntityRenderState extends BlockEntityRenderState {
	public ItemRenderState[] itemRenderStates = new ItemRenderState[3];
	public boolean alignItemsToBottom;
}
