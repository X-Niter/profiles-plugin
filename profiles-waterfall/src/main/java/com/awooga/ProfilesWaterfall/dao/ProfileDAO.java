package com.awooga.ProfilesWaterfall.dao;

import com.awooga.ProfilesWaterfall.ProfilesWaterfall;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;
import java.util.UUID;

public interface ProfileDAO {

	public void storeUUIDOverride(UUID override, UUID user);
	public Optional<UUID> getRealUUID(UUID override);

	public void setUserTargetServer(ProxiedPlayer player, String serverName);
	public Optional<String> getUserTargetServer(ProxiedPlayer player);

	public void storeOriginalUUID(ProxiedPlayer player);
	public Optional<UUID> getOriginalUUID(ProxiedPlayer player);

	public void onUserDisconnect(ProxiedPlayer player);
}
