package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Supplier;
import net.minecraft.network.packet.Packet;
import org.slf4j.Logger;

/**
 * A set of callbacks for sending a packet.
 */
public class PacketCallbacks {
	private static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * {@return a callback that always runs {@code runnable}}
	 */
	public static ChannelFutureListener always(Runnable runnable) {
		return channelFuture -> {
			runnable.run();
			if (!channelFuture.isSuccess()) {
				channelFuture.channel().pipeline().fireExceptionCaught(channelFuture.cause());
			}
		};
	}

	/**
	 * {@return a callback that sends {@code failurePacket} when failed}
	 */
	public static ChannelFutureListener of(Supplier<Packet<?>> failurePacket) {
		return channelFuture -> {
			if (!channelFuture.isSuccess()) {
				Packet<?> packet = (Packet<?>)failurePacket.get();
				if (packet != null) {
					LOGGER.warn("Failed to deliver packet, sending fallback {}", packet.getPacketType(), channelFuture.cause());
					channelFuture.channel().writeAndFlush(packet, channelFuture.channel().voidPromise());
				} else {
					channelFuture.channel().pipeline().fireExceptionCaught(channelFuture.cause());
				}
			}
		};
	}
}
