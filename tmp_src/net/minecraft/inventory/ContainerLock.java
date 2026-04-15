package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public record ContainerLock(ItemPredicate predicate) {
	/**
	 * An empty container lock that can always be opened.
	 */
	public static final ContainerLock EMPTY = new ContainerLock(ItemPredicate.Builder.create().build());
	public static final Codec<ContainerLock> CODEC = ItemPredicate.CODEC.xmap(ContainerLock::new, ContainerLock::predicate);
	public static final String LOCK_KEY = "lock";

	/**
	 * Returns true if this lock can be opened with the key item stack.
	 * <p>
	 * An item stack is a valid key if the stack name matches the key string of this lock,
	 * or if the key string is empty.
	 */
	public boolean canOpen(ItemStack stack) {
		return this.predicate.test(stack);
	}

	/**
	 * Inserts the key string of this lock into the {@code Lock} key of the NBT compound.
	 */
	public void write(WriteView view) {
		if (this != EMPTY) {
			view.put("lock", CODEC, this);
		}
	}

	public boolean checkUnlocked(PlayerEntity player) {
		return player.isSpectator() || this.canOpen(player.getMainHandStack());
	}

	/**
	 * Creates a new {@code ContainerLock} from the {@code Lock} key of the NBT compound.
	 * <p>
	 * If the {@code Lock} key is not present, returns an empty lock.
	 */
	public static ContainerLock read(ReadView view) {
		return (ContainerLock)view.read("lock", CODEC).orElse(EMPTY);
	}
}
