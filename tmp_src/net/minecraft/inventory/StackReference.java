package net.minecraft.inventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Represents a reference to a stack that supports getting and setting.
 * Often for command access. Usually obtained from entities.
 * 
 * <p>Screen handlers also use stack references to pass a mutable cursor
 * stack to some methods.
 * 
 * @see net.minecraft.entity.Entity#getStackReference(int)
 */
public interface StackReference {
	/**
	 * Gets the current item stack.
	 */
	ItemStack get();

	/**
	 * Sets the {@code stack}.
	 * 
	 * @return {@code true} if the setting is successful, {@code false} if rejected
	 * 
	 * @param stack the item stack to set
	 */
	boolean set(ItemStack stack);

	static StackReference of(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return (ItemStack)getter.get();
			}

			@Override
			public boolean set(ItemStack stack) {
				setter.accept(stack);
				return true;
			}
		};
	}

	/**
	 * Creates a stack reference backed by an equipment slot of a living entity and
	 * guarded by a condition for setting stacks into the inventory.
	 * 
	 * @param filter the condition to guard stack setting
	 */
	static StackReference of(LivingEntity entity, EquipmentSlot slot, Predicate<ItemStack> filter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return entity.getEquippedStack(slot);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!filter.test(stack)) {
					return false;
				} else {
					entity.equipStack(slot, stack);
					return true;
				}
			}
		};
	}

	/**
	 * Creates a stack reference backed by an equipment slot of a living entity with
	 * no filter, allowing direct manipulation of the equipment slot.
	 */
	static StackReference of(LivingEntity entity, EquipmentSlot slot) {
		return of(entity, slot, stack -> true);
	}

	/**
	 * Creates a stack reference that points to a specific index of the passed list.
	 */
	static StackReference of(List<ItemStack> stacks, int index) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return (ItemStack)stacks.get(index);
			}

			@Override
			public boolean set(ItemStack stack) {
				stacks.set(index, stack);
				return true;
			}
		};
	}
}
