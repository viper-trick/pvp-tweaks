package net.minecraft.client.world;

import com.mojang.logging.LogUtils;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkLoadMap;
import net.minecraft.world.chunk.ChunkLoadProgress;
import net.minecraft.world.chunk.DeltaChunkLoadProgress;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientChunkLoadProgress implements ChunkLoadProgress {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final long THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30L);
	public static final long WAIT_UNTIL_READY_MILLIS = 500L;
	private final DeltaChunkLoadProgress delegate = new DeltaChunkLoadProgress(true);
	@Nullable
	private ChunkLoadMap chunkLoadMap;
	@Nullable
	private volatile ChunkLoadProgress.Stage stage;
	@Nullable
	private ClientChunkLoadProgress.State state;
	private final long field_61937;

	public ClientChunkLoadProgress() {
		this(0L);
	}

	public ClientChunkLoadProgress(long l) {
		this.field_61937 = l;
	}

	public void setChunkLoadMap(ChunkLoadMap map) {
		this.chunkLoadMap = map;
	}

	public void startWorldLoading(ClientPlayerEntity player, ClientWorld world, WorldRenderer renderer) {
		this.state = new ClientChunkLoadProgress.Start(player, world, renderer, Util.getMeasuringTimeMs() + THIRTY_SECONDS);
	}

	public void tick() {
		if (this.state != null) {
			this.state = this.state.next();
		}
	}

	public boolean isDone() {
		if (this.state instanceof ClientChunkLoadProgress.Wait(long var8)) {
			long var5 = var8;
			if (Util.getMeasuringTimeMs() >= var5 + this.field_61937) {
				return true;
			}
		}

		return false;
	}

	public void initialChunksComing() {
		if (this.state != null) {
			this.state = this.state.initialChunksComing();
		}
	}

	@Override
	public void init(ChunkLoadProgress.Stage stage, int chunks) {
		this.delegate.init(stage, chunks);
		this.stage = stage;
	}

	@Override
	public void progress(ChunkLoadProgress.Stage stage, int fullChunks, int totalChunks) {
		this.delegate.progress(stage, fullChunks, totalChunks);
	}

	@Override
	public void finish(ChunkLoadProgress.Stage stage) {
		this.delegate.finish(stage);
	}

	@Override
	public void initSpawnPos(RegistryKey<World> worldKey, ChunkPos spawnChunk) {
		if (this.chunkLoadMap != null) {
			this.chunkLoadMap.initSpawnPos(worldKey, spawnChunk);
		}
	}

	@Nullable
	public ChunkLoadMap getChunkLoadMap() {
		return this.chunkLoadMap;
	}

	public float getLoadProgress() {
		return this.delegate.getLoadProgress();
	}

	public boolean hasProgress() {
		return this.stage != null;
	}

	@Environment(EnvType.CLIENT)
	record LoadChunks(ClientPlayerEntity player, ClientWorld world, WorldRenderer worldRenderer, long timeoutAfter) implements ClientChunkLoadProgress.State {
		@Override
		public ClientChunkLoadProgress.State next() {
			return (ClientChunkLoadProgress.State)(this.isReady() ? new ClientChunkLoadProgress.Wait(Util.getMeasuringTimeMs()) : this);
		}

		private boolean isReady() {
			if (Util.getMeasuringTimeMs() > this.timeoutAfter) {
				ClientChunkLoadProgress.LOGGER.warn("Timed out while waiting for the client to load chunks, letting the player into the world anyway");
				return true;
			} else {
				BlockPos blockPos = this.player.getBlockPos();
				return !this.world.isOutOfHeightLimit(blockPos.getY()) && !this.player.isSpectator() && this.player.isAlive()
					? this.worldRenderer.isRenderingReady(blockPos)
					: true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	record Start(ClientPlayerEntity player, ClientWorld world, WorldRenderer worldRenderer, long timeoutAfter) implements ClientChunkLoadProgress.State {
		@Override
		public ClientChunkLoadProgress.State initialChunksComing() {
			return new ClientChunkLoadProgress.LoadChunks(this.player, this.world, this.worldRenderer, this.timeoutAfter);
		}
	}

	@Environment(EnvType.CLIENT)
	sealed interface State permits ClientChunkLoadProgress.Start, ClientChunkLoadProgress.LoadChunks, ClientChunkLoadProgress.Wait {
		default ClientChunkLoadProgress.State next() {
			return this;
		}

		default ClientChunkLoadProgress.State initialChunksComing() {
			return this;
		}
	}

	@Environment(EnvType.CLIENT)
	record Wait(long readyAt) implements ClientChunkLoadProgress.State {
	}
}
