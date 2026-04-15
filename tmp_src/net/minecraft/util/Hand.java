package net.minecraft.util;

import net.minecraft.entity.EquipmentSlot;

/**
 * An enum representing an entity's hand.
 * 
 * <p>If the entity is right-handed, {@link #MAIN_HAND} is of {@link Arm#RIGHT},
 * and if the entity is left-handed, {@link #MAIN_HAND} is of {@link Arm#LEFT},
 */
public enum Hand {
	MAIN_HAND,
	OFF_HAND;

	public EquipmentSlot getEquipmentSlot() {
		return this == MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
	}
}
