package net.minecraft.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public interface Tameable {
	@Nullable
	LazyEntityReference<LivingEntity> getOwnerReference();

	World getEntityWorld();

	@Nullable
	default LivingEntity getOwner() {
		return LazyEntityReference.getLivingEntity(this.getOwnerReference(), this.getEntityWorld());
	}

	@Nullable
	default LivingEntity getTopLevelOwner() {
		Set<Object> set = new ObjectArraySet<>();
		LivingEntity livingEntity = this.getOwner();
		set.add(this);

		while (livingEntity instanceof Tameable) {
			Tameable tameable = (Tameable)livingEntity;
			LivingEntity livingEntity2 = tameable.getOwner();
			if (set.contains(livingEntity2)) {
				return null;
			}

			set.add(livingEntity);
			livingEntity = tameable.getOwner();
		}

		return livingEntity;
	}
}
