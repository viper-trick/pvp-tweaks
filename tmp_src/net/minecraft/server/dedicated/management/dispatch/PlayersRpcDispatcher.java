package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.dedicated.management.RpcKickReason;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

public class PlayersRpcDispatcher {
	private static final Text DEFAULT_KICK_REASON = Text.translatable("multiplayer.disconnect.kicked");

	public static List<RpcPlayer> get(ManagementHandlerDispatcher dispatcher) {
		return dispatcher.getPlayerListHandler().getPlayerList().stream().map(RpcPlayer::of).toList();
	}

	public static List<RpcPlayer> kick(ManagementHandlerDispatcher dispatcher, List<PlayersRpcDispatcher.RpcEntry> list, ManagementConnectionId remote) {
		List<RpcPlayer> list2 = new ArrayList();

		for (PlayersRpcDispatcher.RpcEntry rpcEntry : list) {
			ServerPlayerEntity serverPlayerEntity = getPlayer(dispatcher, rpcEntry.player());
			if (serverPlayerEntity != null) {
				dispatcher.getPlayerListHandler().removePlayer(serverPlayerEntity, remote);
				serverPlayerEntity.networkHandler.disconnect((Text)rpcEntry.message.flatMap(RpcKickReason::toText).orElse(DEFAULT_KICK_REASON));
				list2.add(rpcEntry.player());
			}
		}

		return list2;
	}

	@Nullable
	private static ServerPlayerEntity getPlayer(ManagementHandlerDispatcher dispatcher, RpcPlayer player) {
		if (player.id().isPresent()) {
			return dispatcher.getPlayerListHandler().getPlayer((UUID)player.id().get());
		} else {
			return player.name().isPresent() ? dispatcher.getPlayerListHandler().getPlayer((String)player.name().get()) : null;
		}
	}

	public record RpcEntry(RpcPlayer player, Optional<RpcKickReason> message) {
		public static final MapCodec<PlayersRpcDispatcher.RpcEntry> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RpcPlayer.CODEC.codec().fieldOf("player").forGetter(PlayersRpcDispatcher.RpcEntry::player),
					RpcKickReason.CODEC.optionalFieldOf("message").forGetter(PlayersRpcDispatcher.RpcEntry::message)
				)
				.apply(instance, PlayersRpcDispatcher.RpcEntry::new)
		);
	}
}
