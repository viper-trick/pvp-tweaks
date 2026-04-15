package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;

public interface ReadableContainer<T> {
	T get(int x, int y, int z);

	void forEachValue(Consumer<T> action);

	/**
	 * Writes this container to the packet byte buffer.
	 * 
	 * @param buf the packet byte buffer
	 */
	void writePacket(PacketByteBuf buf);

	int getPacketSize();

	@VisibleForTesting
	int getElementBits();

	/**
	 * {@return {@code true} if any object in this container's palette matches
	 * this predicate}
	 */
	boolean hasAny(Predicate<T> predicate);

	void count(PalettedContainer.Counter<T> counter);

	PalettedContainer<T> copy();

	PalettedContainer<T> slice();

	ReadableContainer.Serialized<T> serialize(PaletteProvider<T> provider);

	public interface Reader<T, C extends ReadableContainer<T>> {
		DataResult<C> read(PaletteProvider<T> provider, ReadableContainer.Serialized<T> serialized);
	}

	/**
	 * The storage form of the paletted container in the {@linkplain
	 * PalettedContainer#createCodec codec}. The {@code palette} is the entries
	 * in the palette, but the interpretation of data depends on the palette
	 * provider specified for the codec.
	 * 
	 * @see PalettedContainer#createCodec
	 */
	public record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage, int bitsPerEntry) {
		public static final int MISSING_BITS_PER_ENTRY = -1;

		public Serialized(List<T> paletteEntries, Optional<LongStream> storage) {
			this(paletteEntries, storage, -1);
		}
	}
}
