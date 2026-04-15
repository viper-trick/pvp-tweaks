package net.minecraft.server.network;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.Sets.SetView;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.waypoint.ServerWaypoint;
import net.minecraft.world.waypoint.WaypointHandler;

public class ServerWaypointHandler implements WaypointHandler<ServerWaypoint> {
	private final Set<ServerWaypoint> waypoints = new HashSet();
	private final Set<ServerPlayerEntity> players = new HashSet();
	private final Table<ServerPlayerEntity, ServerWaypoint, ServerWaypoint.WaypointTracker> trackers = HashBasedTable.create();

	public void onTrack(ServerWaypoint serverWaypoint) {
		this.waypoints.add(serverWaypoint);

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			this.refreshTracking(serverPlayerEntity, serverWaypoint);
		}
	}

	public void onUpdate(ServerWaypoint serverWaypoint) {
		if (this.waypoints.contains(serverWaypoint)) {
			Map<ServerPlayerEntity, ServerWaypoint.WaypointTracker> map = Tables.transpose(this.trackers).row(serverWaypoint);
			SetView<ServerPlayerEntity> setView = Sets.difference(this.players, map.keySet());

			for (Entry<ServerPlayerEntity, ServerWaypoint.WaypointTracker> entry : ImmutableSet.copyOf(map.entrySet())) {
				this.refreshTracking((ServerPlayerEntity)entry.getKey(), serverWaypoint, (ServerWaypoint.WaypointTracker)entry.getValue());
			}

			for (ServerPlayerEntity serverPlayerEntity : setView) {
				this.refreshTracking(serverPlayerEntity, serverWaypoint);
			}
		}
	}

	public void onUntrack(ServerWaypoint serverWaypoint) {
		this.trackers.column(serverWaypoint).forEach((player, tracker) -> tracker.untrack());
		Tables.transpose(this.trackers).row(serverWaypoint).clear();
		this.waypoints.remove(serverWaypoint);
	}

	public void addPlayer(ServerPlayerEntity player) {
		this.players.add(player);

		for (ServerWaypoint serverWaypoint : this.waypoints) {
			this.refreshTracking(player, serverWaypoint);
		}

		if (player.hasWaypoint()) {
			this.onTrack((ServerWaypoint)player);
		}
	}

	public void updatePlayerPos(ServerPlayerEntity player) {
		Map<ServerWaypoint, ServerWaypoint.WaypointTracker> map = this.trackers.row(player);
		SetView<ServerWaypoint> setView = Sets.difference(this.waypoints, map.keySet());

		for (Entry<ServerWaypoint, ServerWaypoint.WaypointTracker> entry : ImmutableSet.copyOf(map.entrySet())) {
			this.refreshTracking(player, (ServerWaypoint)entry.getKey(), (ServerWaypoint.WaypointTracker)entry.getValue());
		}

		for (ServerWaypoint serverWaypoint : setView) {
			this.refreshTracking(player, serverWaypoint);
		}
	}

	public void removePlayer(ServerPlayerEntity player) {
		this.trackers.row(player).values().removeIf(tracker -> {
			tracker.untrack();
			return true;
		});
		this.onUntrack((ServerWaypoint)player);
		this.players.remove(player);
	}

	public void clear() {
		this.trackers.values().forEach(ServerWaypoint.WaypointTracker::untrack);
		this.trackers.clear();
	}

	public void refreshTracking(ServerWaypoint waypoint) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			this.refreshTracking(serverPlayerEntity, waypoint);
		}
	}

	public Set<ServerWaypoint> getWaypoints() {
		return this.waypoints;
	}

	private static boolean isLocatorBarEnabled(ServerPlayerEntity player) {
		return player.getEntityWorld().getGameRules().getValue(GameRules.LOCATOR_BAR);
	}

	private void refreshTracking(ServerPlayerEntity player, ServerWaypoint waypoint) {
		if (player != waypoint) {
			if (isLocatorBarEnabled(player)) {
				waypoint.createTracker(player).ifPresentOrElse(tracker -> {
					this.trackers.put(player, waypoint, tracker);
					tracker.track();
				}, () -> {
					ServerWaypoint.WaypointTracker waypointTracker = this.trackers.remove(player, waypoint);
					if (waypointTracker != null) {
						waypointTracker.untrack();
					}
				});
			}
		}
	}

	private void refreshTracking(ServerPlayerEntity player, ServerWaypoint waypoint, ServerWaypoint.WaypointTracker tracker) {
		if (player != waypoint) {
			if (isLocatorBarEnabled(player)) {
				if (!tracker.isInvalid()) {
					tracker.update();
				} else {
					waypoint.createTracker(player).ifPresentOrElse(newTracker -> {
						newTracker.track();
						this.trackers.put(player, waypoint, newTracker);
					}, () -> {
						tracker.untrack();
						this.trackers.remove(player, waypoint);
					});
				}
			}
		}
	}
}
