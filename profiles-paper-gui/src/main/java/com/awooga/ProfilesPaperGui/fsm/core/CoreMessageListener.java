package com.awooga.ProfilesPaperGui.fsm.core;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import com.awooga.ProfilesPaperGui.fsm.events.PlayerUUIDOverrideEvent;
import com.awooga.profiles.ProfilesConstants;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CoreMessageListener implements PluginMessageListener, Listener {

	private final ProfilesPaperGui plugin;

	public CoreMessageListener(ProfilesPaperGui plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
		System.out.println("Got message from player uuid channel - "+channel+" - "+player);
		if(
			!channel.equals( ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS) &&
			!channel.equals( ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.length() - 1))
		) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput( message );
		String subChannel = in.readUTF();
		if(
			subChannel.equalsIgnoreCase(ProfilesConstants.PLAYER_UUID_OVERRIDE_EVENT) ||
			subChannel.equalsIgnoreCase(ProfilesConstants.PLAYER_UUID_OVERRIDE_EVENT.substring(0, ProfilesConstants.PLAYER_UUID_OVERRIDE_EVENT.length() - 1))
		) {
			String originalUuidString = in.readUTF();
			String currentUuidString = in.readUTF();

			UUID originalUuid = UUID.fromString(originalUuidString);
			UUID currentUuid = UUID.fromString(currentUuidString);

			// necessary because the upstream bungee plugin may not use the correct player channel to notify paper, we
			// should find the correct player using the uuid
			Player actualPlayer = plugin.getServer().getPlayer(currentUuid);
			if(actualPlayer == null) {
				actualPlayer = player; // use the player through which bungee communicated to Paper as the user who is getting the assignment
			}

			System.out.println("Got actual player: "+actualPlayer);

			PlayerUUIDOverrideEvent event = PlayerUUIDOverrideEvent.builder()
				.originalUuid(originalUuid)
				.currentUuid(currentUuid)
				.player(actualPlayer)
			.build();

			System.out.println("Dispatching PlayerUUIDOverrideEvent: "+event);

			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}
}
