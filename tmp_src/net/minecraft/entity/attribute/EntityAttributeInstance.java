package net.minecraft.entity.attribute;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * A double-valued attribute.
 */
public class EntityAttributeInstance {
	private final RegistryEntry<EntityAttribute> type;
	private final Map<EntityAttributeModifier.Operation, Map<Identifier, EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(
		EntityAttributeModifier.Operation.class
	);
	private final Map<Identifier, EntityAttributeModifier> idToModifiers = new Object2ObjectArrayMap<>();
	private final Map<Identifier, EntityAttributeModifier> persistentModifiers = new Object2ObjectArrayMap<>();
	private double baseValue;
	private boolean dirty = true;
	private double value;
	private final Consumer<EntityAttributeInstance> updateCallback;

	public EntityAttributeInstance(RegistryEntry<EntityAttribute> type, Consumer<EntityAttributeInstance> updateCallback) {
		this.type = type;
		this.updateCallback = updateCallback;
		this.baseValue = type.value().getDefaultValue();
	}

	public RegistryEntry<EntityAttribute> getAttribute() {
		return this.type;
	}

	/**
	 * Gets the base value of this attribute instance.
	 * This is the value before any attribute modifiers are applied.
	 */
	public double getBaseValue() {
		return this.baseValue;
	}

	public void setBaseValue(double baseValue) {
		if (baseValue != this.baseValue) {
			this.baseValue = baseValue;
			this.onUpdate();
		}
	}

	@VisibleForTesting
	Map<Identifier, EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
		return (Map<Identifier, EntityAttributeModifier>)this.operationToModifiers.computeIfAbsent(operation, operationx -> new Object2ObjectOpenHashMap());
	}

	public Set<EntityAttributeModifier> getModifiers() {
		return ImmutableSet.copyOf(this.idToModifiers.values());
	}

	public Set<EntityAttributeModifier> getPersistentModifiers() {
		return ImmutableSet.copyOf(this.persistentModifiers.values());
	}

	@Nullable
	public EntityAttributeModifier getModifier(Identifier id) {
		return (EntityAttributeModifier)this.idToModifiers.get(id);
	}

	public boolean hasModifier(Identifier id) {
		return this.idToModifiers.get(id) != null;
	}

	private void addModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.putIfAbsent(modifier.id(), modifier);
		if (entityAttributeModifier != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			this.getModifiers(modifier.operation()).put(modifier.id(), modifier);
			this.onUpdate();
		}
	}

	public void updateModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.put(modifier.id(), modifier);
		if (modifier != entityAttributeModifier) {
			this.getModifiers(modifier.operation()).put(modifier.id(), modifier);
			this.onUpdate();
		}
	}

	/**
	 * Adds a temporary attribute modifier.
	 * The modifier will not be serialized.
	 */
	public void addTemporaryModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
	}

	public void overwritePersistentModifier(EntityAttributeModifier modifier) {
		this.removeModifier(modifier.id());
		this.addModifier(modifier);
		this.persistentModifiers.put(modifier.id(), modifier);
	}

	public void addPersistentModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
		this.persistentModifiers.put(modifier.id(), modifier);
	}

	public void addPersistentModifiers(Collection<EntityAttributeModifier> modifiers) {
		for (EntityAttributeModifier entityAttributeModifier : modifiers) {
			this.addPersistentModifier(entityAttributeModifier);
		}
	}

	protected void onUpdate() {
		this.dirty = true;
		this.updateCallback.accept(this);
	}

	public void removeModifier(EntityAttributeModifier modifier) {
		this.removeModifier(modifier.id());
	}

	public boolean removeModifier(Identifier id) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.remove(id);
		if (entityAttributeModifier == null) {
			return false;
		} else {
			this.getModifiers(entityAttributeModifier.operation()).remove(id);
			this.persistentModifiers.remove(id);
			this.onUpdate();
			return true;
		}
	}

	public void clearModifiers() {
		for (EntityAttributeModifier entityAttributeModifier : this.getModifiers()) {
			this.removeModifier(entityAttributeModifier);
		}
	}

	public double getValue() {
		if (this.dirty) {
			this.value = this.computeValue();
			this.dirty = false;
		}

		return this.value;
	}

	/**
	 * Computes this attribute's value, taking modifiers into account.
	 * 
	 * <p>Attribute modifiers are applied in order by operation:
	 * <ul><li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_VALUE ADD_VALUE} // Adds the value of the modifier to the attribute's base value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_MULTIPLIED_BASE ADD_MULTIPLIED_BASE} // Multiplies the value of the modifier to the attributes base value, and then adds it to the total value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_MULTIPLIED_TOTAL ADD_MULTIPLIED_TOTAL} // Adds 1 to the value of the attribute modifier. Then multiplies the attribute's value by the total value of the attribute after addition and multiplication of the base value occur.</li>
	 * </ul>
	 */
	private double computeValue() {
		double d = this.getBaseValue();

		for (EntityAttributeModifier entityAttributeModifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_VALUE)) {
			d += entityAttributeModifier.value();
		}

		double e = d;

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
			e += d * entityAttributeModifier2.value();
		}

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
			e *= 1.0 + entityAttributeModifier2.value();
		}

		return this.type.value().clamp(e);
	}

	private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
		return ((Map)this.operationToModifiers.getOrDefault(operation, Map.of())).values();
	}

	/**
	 * Copies the values of an attribute to this attribute.
	 * 
	 * <p>Temporary modifiers are copied when using the operation.
	 */
	public void setFrom(EntityAttributeInstance other) {
		this.baseValue = other.baseValue;
		this.idToModifiers.clear();
		this.idToModifiers.putAll(other.idToModifiers);
		this.persistentModifiers.clear();
		this.persistentModifiers.putAll(other.persistentModifiers);
		this.operationToModifiers.clear();
		other.operationToModifiers.forEach((operation, modifiers) -> this.getModifiers(operation).putAll(modifiers));
		this.onUpdate();
	}

	public EntityAttributeInstance.Packed pack() {
		return new EntityAttributeInstance.Packed(this.type, this.baseValue, List.copyOf(this.persistentModifiers.values()));
	}

	public void unpack(EntityAttributeInstance.Packed packed) {
		this.baseValue = packed.baseValue;

		for (EntityAttributeModifier entityAttributeModifier : packed.modifiers) {
			this.idToModifiers.put(entityAttributeModifier.id(), entityAttributeModifier);
			this.getModifiers(entityAttributeModifier.operation()).put(entityAttributeModifier.id(), entityAttributeModifier);
			this.persistentModifiers.put(entityAttributeModifier.id(), entityAttributeModifier);
		}

		this.onUpdate();
	}

	public record Packed(RegistryEntry<EntityAttribute> attribute, double baseValue, List<EntityAttributeModifier> modifiers) {
		public static final Codec<EntityAttributeInstance.Packed> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Registries.ATTRIBUTE.getEntryCodec().fieldOf("id").forGetter(EntityAttributeInstance.Packed::attribute),
					Codec.DOUBLE.fieldOf("base").orElse(0.0).forGetter(EntityAttributeInstance.Packed::baseValue),
					EntityAttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(EntityAttributeInstance.Packed::modifiers)
				)
				.apply(instance, EntityAttributeInstance.Packed::new)
		);
		public static final Codec<List<EntityAttributeInstance.Packed>> LIST_CODEC = CODEC.listOf();
	}
}
