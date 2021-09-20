package com.awooga.ProfilesPaperGui.dao.impl;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import com.awooga.ProfilesPaperGui.sql.DbConnection;
import com.awooga.ProfilesPaperGui.dao.PlayerProfilesDAO;
import com.awooga.ProfilesPaperGui.dao.ProfileEntity;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlayerProfilesDAOImpl implements PlayerProfilesDAO {

	private final ProfilesPaperGui plugin;
	private final FileConfiguration config;
	public PlayerProfilesDAOImpl(ProfilesPaperGui main) {
		plugin = main;
		config = main.getConfig();
	}

	final DbConnection gradeConnection = ProfilesPaperGui.getInstance().getDatabaseManager().getGradeConnection();

	HashSet<UUID> brandNewUuids = new HashSet<>();
	HashMap<UUID, UUID> originalUuidMap = new HashMap<>();
	HashSet<UUID> genuineUuids = new HashSet<>();

	@SneakyThrows(SQLException.class)
	@Override
	public void applyMigrations() {
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement(
				"CREATE TABLE IF NOT EXISTS profiles (\n" +
			"  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
			"  `playerUuid` char(36) NOT NULL,\n" +
			"  `profileUuid` char(36) NOT NULL,\n" +
			"  `deleted` tinyint(4) NOT NULL DEFAULT false,\n" +
			"  `cachedPlaceholderTitle` TINYTEXT,\n" +
			"  `cachedPlaceholderBody` TINYTEXT,\n" +
			"  PRIMARY KEY (`id`),\n" +
			"  KEY `playerUuid` (`playerUuid`)\n" +
			") ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4"
		);
		stmt.execute();
	}

	@Override
	public UUID getGenuineUUID(OfflinePlayer player) {
		UUID currentUuid = player.getUniqueId();
		UUID originalUuid = originalUuidMap.get(currentUuid);
		//Bukkit.getLogger().info("Trying to determine genuine uuid - " + currentUuid + " --- " + originalUuid);
		if(originalUuid != null) {
			return genuineUuids.contains(originalUuid) ? originalUuid : currentUuid;
		}
		return currentUuid;
	}

	@Override
	public UUID getProfileUUID(OfflinePlayer player) {
		UUID genuineUUID = getGenuineUUID(player);
		UUID profileUUID = originalUuidMap.get(genuineUUID);
		return profileUUID != null ? profileUUID : genuineUUID;
	}

	@SneakyThrows
	@Override
	public UUID createNewProfile(Player player){
		UUID genuineUuid = getGenuineUUID(player);
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement("INSERT INTO profiles (playerUuid, profileUuid) VALUES (?, ?)");
		UUID newUuid = UUID.randomUUID();
		stmt.setString(1, genuineUuid.toString());
		stmt.setString(2, newUuid.toString());
		int result = stmt.executeUpdate();

		if (result == 0) {
			throw new Exception("Couldn't create new profile for genuineUuid: " + genuineUuid);
		}
		return newUuid;
	}

	@SneakyThrows(SQLException.class)
	@Override
	public void deleteProfile(Player player, UUID profileUuid) {
		UUID genuineUuid = getGenuineUUID(player);
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement("UPDATE profiles SET deleted=true WHERE playerUuid=? AND profileUuid=?");
		stmt.setString(1, genuineUuid.toString());
		stmt.setString(2, profileUuid.toString());
        stmt.execute();
    }

	@SneakyThrows(SQLException.class)
	@Override
	public UUID[] getProfilesByGenuineUUID(UUID genuineUuid) {
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement("SELECT profileUuid FROM profiles WHERE playerUuid=? AND deleted=false");
		stmt.setString(1, genuineUuid.toString());
		ResultSet resultSet = stmt.executeQuery();
		ArrayList<UUID> result = new ArrayList();

		if(!config.getBoolean("options.disableMojangProfile", true)) {
			result.add(genuineUuid);
		}
		while (resultSet.next()) {
			result.add(UUID.fromString(resultSet.getString(1)));
		}

		return result.toArray(new UUID[result.size()]);
	}

	@SneakyThrows(SQLException.class)
	@Override
	public List<@NotNull ProfileEntity> getProfileEntitiesByGenuineUUID(UUID genuineUuid) {
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement("SELECT * FROM profiles WHERE playerUuid=? AND deleted=false"
			, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, genuineUuid.toString());
		ResultSet resultSet = stmt.executeQuery();
		List<ProfileEntity> result = new ArrayList();

		if(!config.getBoolean("options.disableMojangProfile", true)) {
			ProfileEntity ent = ProfileEntity.builder()
				.id(-1L)
				.cachedPlaceholderTitle("Not Supported")
				.cachedPlaceholderBody("options.disableMojangProfile=false is not supported with options.preferCachedPlaceholders=true. Change one of them")
				.deleted(false)
				.playerUuid(genuineUuid)
				.profileUuid(genuineUuid)
			.build();
			result.add(ent);
		}

		while (resultSet.next()) {
			ProfileEntity ent = ProfileEntity.builder()
				.id(resultSet.getLong("id"))
				.cachedPlaceholderTitle(resultSet.getString("cachedPlaceholderTitle"))
				.cachedPlaceholderBody(resultSet.getString("cachedPlaceholderBody"))
				.deleted(resultSet.getBoolean("deleted"))
				.playerUuid(UUID.fromString(resultSet.getString("playerUuid")))
				.profileUuid(UUID.fromString(resultSet.getString("profileUuid")))
			.build();
			result.add(ent);
		}

		return result;
	}

	@Override
	public void save(ProfileEntity ent) {
		if(ent.getId() == null) {
			this.insert(ent);
		} else {
			this.update(ent);
		}
	}

	@SneakyThrows(SQLException.class)
	private void update(ProfileEntity ent) {
		PreparedStatement stmt = gradeConnection.getConnection().prepareStatement(
			"UPDATE profiles\n" +
			"SET\n" +
			"    playerUuid=?,\n" +
			"    profileUuid=?,\n" +
			"    deleted=?,\n" +
			"    cachedPlaceholderTitle=?,\n" +
			"    cachedPlaceholderBody=?\n" +
			"WHERE id=?;"
		);
		stmt.setString(1, ent.getPlayerUuid().toString());
		stmt.setString(2, ent.getProfileUuid().toString());
		stmt.setBoolean(3, ent.isDeleted());
		stmt.setString(4, ent.getCachedPlaceholderTitle());
		stmt.setString(5, ent.getCachedPlaceholderBody());
		stmt.setLong(6, ent.getId());
		stmt.execute();
	}

	@SneakyThrows(Exception.class)
	private void insert(ProfileEntity ent) {
		throw new Exception("Unimplemented: PlayerProfilesDAOImpl.insert");
	}

	@Override
	public void storeUuidOverride(UUID original, UUID override) {
		originalUuidMap.put(original, override);
		originalUuidMap.put(override, original);
		genuineUuids.add(original);
	}

	@Override
	public void onUserDisconnect(Player player) {
		UUID currentUuid = player.getUniqueId();
		UUID originalUuid = originalUuidMap.get(currentUuid);
		if(originalUuid != null) {
			originalUuidMap.remove(originalUuid);
			originalUuidMap.remove(currentUuid);
		}
		genuineUuids.remove(originalUuid);
		genuineUuids.remove(currentUuid);
	}

	@Override
	public void addBrandNewProfileId(UUID uuid) {
		brandNewUuids.add(uuid);
	}

	@Override
	public boolean isProfileIdBrandNew(UUID uuid) {
		return brandNewUuids.contains(uuid);
	}

	@Override
	public void removeBrandNewProfileId(UUID uuid) {
		brandNewUuids.remove(uuid);
	}
}
