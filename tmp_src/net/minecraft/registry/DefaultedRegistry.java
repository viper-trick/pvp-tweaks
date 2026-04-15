package net.minecraft.registry;

import net.minecraft.util.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DefaultedRegistry<T> extends Registry<T> {
	@NonNull
	@Override
	Identifier getId(T value);

	@NonNull
	@Override
	T get(@Nullable Identifier id);

	@NonNull
	@Override
	T get(int index);

	Identifier getDefaultId();
}
