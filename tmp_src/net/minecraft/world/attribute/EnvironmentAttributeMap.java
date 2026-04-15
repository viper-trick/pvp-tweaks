package net.minecraft.world.attribute;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public final class EnvironmentAttributeMap {
	public static final EnvironmentAttributeMap EMPTY = new EnvironmentAttributeMap(Map.of());
	public static final Codec<EnvironmentAttributeMap> CODEC = Codec.lazyInitialized(
		() -> Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(EnvironmentAttributeMap.Entry::createCodec))
			.xmap(EnvironmentAttributeMap::new, map -> map.entries)
	);
	public static final Codec<EnvironmentAttributeMap> NETWORK_CODEC = CODEC.xmap(
		EnvironmentAttributeMap::retainSyncedAttributes, EnvironmentAttributeMap::retainSyncedAttributes
	);
	/**
	 * Accepts positional attributes only.
	 */
	public static final Codec<EnvironmentAttributeMap> POSITIONAL_CODEC = CODEC.validate(map -> {
		List<EnvironmentAttribute<?>> list = map.keySet().stream().filter(attribute -> !attribute.isPositional()).toList();
		return !list.isEmpty() ? DataResult.error(() -> "The following attributes cannot be positional: " + list) : DataResult.success(map);
	});
	final Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries;

	private static EnvironmentAttributeMap retainSyncedAttributes(EnvironmentAttributeMap map) {
		return new EnvironmentAttributeMap(Map.copyOf(Maps.filterKeys(map.entries, EnvironmentAttribute::isSynced)));
	}

	EnvironmentAttributeMap(Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries) {
		this.entries = entries;
	}

	public static EnvironmentAttributeMap.Builder builder() {
		return new EnvironmentAttributeMap.Builder();
	}

	@Nullable
	public <Value> EnvironmentAttributeMap.Entry<Value, ?> getEntry(EnvironmentAttribute<Value> key) {
		return (EnvironmentAttributeMap.Entry<Value, ?>)this.entries.get(key);
	}

	public <Value> Value apply(EnvironmentAttribute<Value> key, Value value) {
		EnvironmentAttributeMap.Entry<Value, ?> entry = this.getEntry(key);
		return entry != null ? entry.apply(value) : value;
	}

	public boolean containsKey(EnvironmentAttribute<?> key) {
		return this.entries.containsKey(key);
	}

	public Set<EnvironmentAttribute<?>> keySet() {
		return this.entries.keySet();
	}

	public boolean equals(Object o) {
		return o == this ? true : o instanceof EnvironmentAttributeMap environmentAttributeMap && this.entries.equals(environmentAttributeMap.entries);
	}

	public int hashCode() {
		return this.entries.hashCode();
	}

	public String toString() {
		return this.entries.toString();
	}

	public static class Builder {
		private final Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries = new HashMap();

		Builder() {
		}

		public EnvironmentAttributeMap.Builder addAll(EnvironmentAttributeMap map) {
			this.entries.putAll(map.entries);
			return this;
		}

		public <Value, Parameter> EnvironmentAttributeMap.Builder with(
			EnvironmentAttribute<Value> key, EnvironmentAttributeModifier<Value, Parameter> modifier, Parameter param
		) {
			key.getType().validate(modifier);
			this.entries.put(key, new EnvironmentAttributeMap.Entry<>(param, modifier));
			return this;
		}

		public <Value> EnvironmentAttributeMap.Builder with(EnvironmentAttribute<Value> key, Value value) {
			return this.with(key, EnvironmentAttributeModifier.override(), value);
		}

		public EnvironmentAttributeMap build() {
			return this.entries.isEmpty() ? EnvironmentAttributeMap.EMPTY : new EnvironmentAttributeMap(Map.copyOf(this.entries));
		}
	}

	public record Entry<Value, Argument>(Argument argument, EnvironmentAttributeModifier<Value, Argument> modifier) {
		private static <Value> Codec<EnvironmentAttributeMap.Entry<Value, ?>> createCodec(EnvironmentAttribute<Value> attribute) {
			Codec<EnvironmentAttributeMap.Entry<Value, ?>> codec = attribute.getType()
				.modifierCodec()
				.dispatch(
					"modifier",
					EnvironmentAttributeMap.Entry::modifier,
					Util.memoize(
						(Function<? super EnvironmentAttributeModifier<Value, ?>, ? extends MapCodec<? extends EnvironmentAttributeMap.Entry<Value, ?>>>)(modifier -> createModifierDependentCodec(
							attribute, modifier
						))
					)
				);
			return Codec.either(attribute.getCodec(), codec)
				.xmap(
					either -> either.map(value -> new EnvironmentAttributeMap.Entry<>(value, EnvironmentAttributeModifier.override()), entry -> entry),
					entry -> entry.modifier == EnvironmentAttributeModifier.override() ? Either.left(entry.argument()) : Either.right(entry)
				);
		}

		private static <Value, Argument> MapCodec<EnvironmentAttributeMap.Entry<Value, Argument>> createModifierDependentCodec(
			EnvironmentAttribute<Value> attribute, EnvironmentAttributeModifier<Value, Argument> modifier
		) {
			return RecordCodecBuilder.mapCodec(
				instance -> instance.group(modifier.argumentCodec(attribute).fieldOf("argument").forGetter(EnvironmentAttributeMap.Entry::argument))
					.apply(instance, argument -> new EnvironmentAttributeMap.Entry<>(argument, modifier))
			);
		}

		public Value apply(Value value) {
			return this.modifier.apply(value, this.argument);
		}
	}
}
