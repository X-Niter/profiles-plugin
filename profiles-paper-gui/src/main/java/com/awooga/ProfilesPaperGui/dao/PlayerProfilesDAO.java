package com.awooga.ProfilesPaperGui.dao;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerProfilesDAO {

	boolean isProfileIdBrandNew(UUID uuid);


	void applyMigrations();
	void save(ProfileEntity ent);
	void onUserDisconnect(Player player);
	void addBrandNewProfileId(UUID uuid);
	void removeBrandNewProfileId(UUID uuid);
	void deleteProfile(Player player, UUID profile);
	void storeUuidOverride(UUID current, UUID override);


	UUID createNewProfile(Player player);
	UUID getGenuineUUID(OfflinePlayer player);
	UUID getProfileUUID(OfflinePlayer player);
	UUID[] getProfilesByGenuineUUID(UUID uuid);


	List<ProfileEntity> getProfileEntitiesByGenuineUUID(UUID uuid);
}
