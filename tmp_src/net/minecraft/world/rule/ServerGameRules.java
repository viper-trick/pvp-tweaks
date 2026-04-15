package net.minecraft.world.rule;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import org.jspecify.annotations.Nullable;

public final class ServerGameRules {
	public static final Codec<ServerGameRules> CODEC = Codec.dispatchedMap(Registries.GAME_RULE.getCodec(), GameRule::getCodec)
		.xmap(ServerGameRules::of, ServerGameRules::getRuleValues);
	private final Reference2ObjectMap<GameRule<?>, Object> ruleValues;

	ServerGameRules(Reference2ObjectMap<GameRule<?>, Object> ruleValues) {
		this.ruleValues = ruleValues;
	}

	private static ServerGameRules of(Map<GameRule<?>, Object> ruleValues) {
		return new ServerGameRules(new Reference2ObjectOpenHashMap<>(ruleValues));
	}

	public static ServerGameRules of() {
		return new ServerGameRules(new Reference2ObjectOpenHashMap<>());
	}

	public static ServerGameRules ofDefault(Stream<GameRule<?>> rules) {
		Reference2ObjectOpenHashMap<GameRule<?>, Object> reference2ObjectOpenHashMap = new Reference2ObjectOpenHashMap<>();
		rules.forEach(gameRule -> reference2ObjectOpenHashMap.put(gameRule, gameRule.getDefaultValue()));
		return new ServerGameRules(reference2ObjectOpenHashMap);
	}

	public static ServerGameRules copyOf(ServerGameRules rules) {
		return new ServerGameRules(new Reference2ObjectOpenHashMap<>(rules.ruleValues));
	}

	public boolean contains(GameRule<?> rule) {
		return this.ruleValues.containsKey(rule);
	}

	@Nullable
	public <T> T get(GameRule<T> rule) {
		return (T)this.ruleValues.get(rule);
	}

	public <T> void put(GameRule<T> rule, T value) {
		this.ruleValues.put(rule, value);
	}

	@Nullable
	public <T> T remove(GameRule<T> rule) {
		return (T)this.ruleValues.remove(rule);
	}

	public Set<GameRule<?>> keySet() {
		return this.ruleValues.keySet();
	}

	public int size() {
		return this.ruleValues.size();
	}

	public String toString() {
		return this.ruleValues.toString();
	}

	public ServerGameRules withOverride(ServerGameRules override) {
		ServerGameRules serverGameRules = copyOf(this);
		serverGameRules.copyFrom(override, rule -> true);
		return serverGameRules;
	}

	public void copyFrom(ServerGameRules rules, Predicate<GameRule<?>> predicate) {
		for (GameRule<?> gameRule : rules.keySet()) {
			if (predicate.test(gameRule)) {
				setFrom(rules, gameRule, this);
			}
		}
	}

	private static <T> void setFrom(ServerGameRules oldRules, GameRule<T> rule, ServerGameRules newRules) {
		newRules.put(rule, (T)Objects.requireNonNull(oldRules.get(rule)));
	}

	private Reference2ObjectMap<GameRule<?>, Object> getRuleValues() {
		return this.ruleValues;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o != null && o.getClass() == this.getClass()) {
			ServerGameRules serverGameRules = (ServerGameRules)o;
			return Objects.equals(this.ruleValues, serverGameRules.ruleValues);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.ruleValues});
	}

	public static class Builder {
		final Reference2ObjectMap<GameRule<?>, Object> ruleValues = new Reference2ObjectOpenHashMap<>();

		public <T> ServerGameRules.Builder put(GameRule<T> rule, T value) {
			this.ruleValues.put(rule, value);
			return this;
		}

		public ServerGameRules build() {
			return new ServerGameRules(this.ruleValues);
		}
	}
}
