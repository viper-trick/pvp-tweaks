package net.minecraft.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityQueriable;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jspecify.annotations.Nullable;

public final class LazyEntityReference<StoredEntityType extends UniquelyIdentifiable> {
	private static final Codec<? extends LazyEntityReference<?>> CODEC = Uuids.INT_STREAM_CODEC.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
	private static final PacketCodec<ByteBuf, ? extends LazyEntityReference<?>> PACKET_CODEC = Uuids.PACKET_CODEC
		.xmap(LazyEntityReference::new, LazyEntityReference::getUuid);
	private Either<UUID, StoredEntityType> value;

	public static <Type extends UniquelyIdentifiable> Codec<LazyEntityReference<Type>> createCodec() {
		return (Codec<LazyEntityReference<Type>>)CODEC;
	}

	public static <Type extends UniquelyIdentifiable> PacketCodec<ByteBuf, LazyEntityReference<Type>> createPacketCodec() {
		return (PacketCodec<ByteBuf, LazyEntityReference<Type>>)PACKET_CODEC;
	}

	private LazyEntityReference(StoredEntityType value) {
		this.value = Either.right(value);
	}

	private LazyEntityReference(UUID value) {
		this.value = Either.left(value);
	}

	@Nullable
	public static <T extends UniquelyIdentifiable> LazyEntityReference<T> of(@Nullable T object) {
		return object != null ? new LazyEntityReference<>(object) : null;
	}

	public static <T extends UniquelyIdentifiable> LazyEntityReference<T> ofUUID(UUID uuid) {
		return new LazyEntityReference<>(uuid);
	}

	public UUID getUuid() {
		return this.value.map(uuid -> uuid, UniquelyIdentifiable::getUuid);
	}

	@Nullable
	public StoredEntityType resolve(EntityQueriable<? extends UniquelyIdentifiable> world, Class<StoredEntityType> type) {
		Optional<StoredEntityType> optional = this.value.right();
		if (optional.isPresent()) {
			StoredEntityType uniquelyIdentifiable = (StoredEntityType)optional.get();
			if (!uniquelyIdentifiable.isRemoved()) {
				return uniquelyIdentifiable;
			}

			this.value = Either.left(uniquelyIdentifiable.getUuid());
		}

		Optional<UUID> optional2 = this.value.left();
		if (optional2.isPresent()) {
			StoredEntityType uniquelyIdentifiable2 = this.cast(world.lookup((UUID)optional2.get()), type);
			if (uniquelyIdentifiable2 != null && !uniquelyIdentifiable2.isRemoved()) {
				this.value = Either.right(uniquelyIdentifiable2);
				return uniquelyIdentifiable2;
			}
		}

		return null;
	}

	@Nullable
	public StoredEntityType getEntityByClass(World world, Class<StoredEntityType> clazz) {
		return PlayerEntity.class.isAssignableFrom(clazz) ? this.resolve(world::getPlayerAnyDimension, clazz) : this.resolve(world::getEntityAnyDimension, clazz);
	}

	@Nullable
	private StoredEntityType cast(@Nullable UniquelyIdentifiable entity, Class<StoredEntityType> clazz) {
		return (StoredEntityType)(entity != null && clazz.isAssignableFrom(entity.getClass()) ? clazz.cast(entity) : null);
	}

	public boolean uuidEquals(StoredEntityType o) {
		return this.getUuid().equals(o.getUuid());
	}

	public void writeData(WriteView view, String key) {
		view.put(key, Uuids.INT_STREAM_CODEC, this.getUuid());
	}

	public static void writeData(@Nullable LazyEntityReference<?> entityRef, WriteView view, String key) {
		if (entityRef != null) {
			entityRef.writeData(view, key);
		}
	}

	@Nullable
	public static <StoredEntityType extends UniquelyIdentifiable> StoredEntityType resolve(
		@Nullable LazyEntityReference<StoredEntityType> entity, World world, Class<StoredEntityType> type
	) {
		return entity != null ? entity.getEntityByClass(world, type) : null;
	}

	@Nullable
	public static Entity getEntity(@Nullable LazyEntityReference<Entity> entityReference, World world) {
		return resolve(entityReference, world, Entity.class);
	}

	@Nullable
	public static LivingEntity getLivingEntity(@Nullable LazyEntityReference<LivingEntity> livingReference, World world) {
		return resolve(livingReference, world, LivingEntity.class);
	}

	@Nullable
	public static PlayerEntity getPlayerEntity(@Nullable LazyEntityReference<PlayerEntity> playerReference, World world) {
		return resolve(playerReference, world, PlayerEntity.class);
	}

	@Nullable
	public static <StoredEntityType extends UniquelyIdentifiable> LazyEntityReference<StoredEntityType> fromData(ReadView view, String key) {
		return (LazyEntityReference<StoredEntityType>)view.read(key, createCodec()).orElse(null);
	}

	@Nullable
	public static <StoredEntityType extends UniquelyIdentifiable> LazyEntityReference<StoredEntityType> fromDataOrPlayerName(
		ReadView view, String key, World world
	) {
		Optional<UUID> optional = view.read(key, Uuids.INT_STREAM_CODEC);
		return optional.isPresent()
			? ofUUID((UUID)optional.get())
			: (LazyEntityReference)view.getOptionalString(key)
				.map(name -> ServerConfigHandler.getPlayerUuidByName(world.getServer(), name))
				.map(LazyEntityReference::new)
				.orElse(null);
	}

	public boolean equals(Object object) {
		return object == this ? true : object instanceof LazyEntityReference<?> lazyEntityReference && this.getUuid().equals(lazyEntityReference.getUuid());
	}

	public int hashCode() {
		return this.getUuid().hashCode();
	}
}
