package com.awooga.ProfilesWaterfall.dao.impl;

import com.awooga.ProfilesWaterfall.ProfilesWaterfall;
import com.awooga.ProfilesWaterfall.dao.ProfileDAO;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ProfileDAOImpl implements ProfileDAO {

	ProfilesWaterfall plugin;
	public ProfileDAOImpl(ProfilesWaterfall main) {
		plugin = main;
	}

	HashMap<ProxiedPlayer, String> targetServerMap = new HashMap<>();

	// two way mapping of uuids. contains entries for both real -> fake and fake -> real
	HashMap<UUID, UUID> genuineUuidMap = new HashMap<>();

	HashMap<ProxiedPlayer, UUID> originalUuidMap = new HashMap<>();

	@Override
	public Optional<UUID> getRealUUID(UUID override) {
		return Optional.ofNullable(genuineUuidMap.get(override));
	}

	@Override
	public void storeUUIDOverride(UUID override, UUID user) {
		UUID storedOverride = genuineUuidMap.get(user);
		if(storedOverride != null) {
			genuineUuidMap.remove(storedOverride);
		}
		genuineUuidMap.remove(override);
		genuineUuidMap.remove(user);
		genuineUuidMap.put(override, user);
		genuineUuidMap.put(user, override);
	}

	@Override
	public void setUserTargetServer(ProxiedPlayer player, String serverName) {
		if(serverName == null) {
			targetServerMap.remove(player);
		} else {
			targetServerMap.put(player, serverName);
		}
	}

	@Override
	public Optional<String> getUserTargetServer(ProxiedPlayer player) {
		return Optional.ofNullable(targetServerMap.get(player));
	}

	@Override
	public void storeOriginalUUID(ProxiedPlayer player) {
		if(originalUuidMap.get(player) == null) {
			originalUuidMap.put(player, player.getUniqueId());
		}
	}

	@Override
	public Optional<UUID> getOriginalUUID(ProxiedPlayer player) {
		return Optional.of(Optional.ofNullable(originalUuidMap.get(player)).orElseGet(Optional.ofNullable(player.getUniqueId())::get));
	}

	@Override
	public void onUserDisconnect(ProxiedPlayer player) {
		Optional<UUID> maybeOriginalUUID = getOriginalUUID(player);

		if(maybeOriginalUUID.isPresent()) {
			UUID originalUUID = maybeOriginalUUID.get();
			UUID storedOverride = genuineUuidMap.get(originalUUID);
			if (storedOverride != null) {
				genuineUuidMap.remove(storedOverride);
			}
			genuineUuidMap.remove(originalUUID);
		}

		originalUuidMap.remove(player);
		targetServerMap.remove(player);
	}
}
