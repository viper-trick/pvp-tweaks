package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class StopCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("stop").requires(CommandManager.requirePermissionLevel(CommandManager.OWNERS_CHECK)).executes(context -> {
			context.getSource().sendFeedback(() -> Text.translatable("commands.stop.stopping"), true);
			context.getSource().getServer().stop(false);
			return 1;
		}));
	}
}
