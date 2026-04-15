package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.waypoint.ServerWaypoint;

public class WaypointArgument {
	public static final SimpleCommandExceptionType INVALID_WAYPOINT_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.waypoint.invalid"));

	public static ServerWaypoint getWaypoint(CommandContext<ServerCommandSource> context, String argument) throws CommandSyntaxException {
		if (context.<EntitySelector>getArgument(argument, EntitySelector.class).getEntity(context.getSource()) instanceof ServerWaypoint serverWaypoint) {
			return serverWaypoint;
		} else {
			throw INVALID_WAYPOINT_EXCEPTION.create();
		}
	}
}
