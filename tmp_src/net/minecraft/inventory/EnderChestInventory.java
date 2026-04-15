package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jspecify.annotations.Nullable;

/**
 * Represents an inventory used for ender chests.
 * A new instance is created for each player.
 */
public class EnderChestInventory extends SimpleInventory {
	@Nullable
	private EnderChestBlockEntity activeBlockEntity;

	public EnderChestInventory() {
		super(27);
	}

	/**
	 * Sets the block entity the player is using to access the inventory to {@code
	 * blockEntity}. The block entity is used to delegate {@link #canPlayerUse},
	 * {@link #onOpen}, and {@link #onClose}.
	 */
	public void setActiveBlockEntity(EnderChestBlockEntity blockEntity) {
		this.activeBlockEntity = blockEntity;
	}

	/**
	 * {@return whether this inventory is being accessed from {@code blockEntity}}
	 */
	public boolean isActiveBlockEntity(EnderChestBlockEntity blockEntity) {
		return this.activeBlockEntity == blockEntity;
	}

	public void readData(ReadView.TypedListReadView<StackWithSlot> list) {
		for (int i = 0; i < this.size(); i++) {
			this.setStack(i, ItemStack.EMPTY);
		}

		for (StackWithSlot stackWithSlot : list) {
			if (stackWithSlot.isValidSlot(this.size())) {
				this.setStack(stackWithSlot.slot(), stackWithSlot.stack());
			}
		}
	}

	public void writeData(WriteView.ListAppender<StackWithSlot> list) {
		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (!itemStack.isEmpty()) {
				list.add(new StackWithSlot(i, itemStack));
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.activeBlockEntity != null && !this.activeBlockEntity.canPlayerUse(player) ? false : super.canPlayerUse(player);
	}

	@Override
	public void onOpen(ContainerUser user) {
		if (this.activeBlockEntity != null) {
			this.activeBlockEntity.onOpen(user);
		}

		super.onOpen(user);
	}

	@Override
	public void onClose(ContainerUser user) {
		if (this.activeBlockEntity != null) {
			this.activeBlockEntity.onClose(user);
		}

		super.onClose(user);
		this.activeBlockEntity = null;
	}
}
