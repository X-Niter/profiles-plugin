package com.awooga.ProfilesPaperGui;

import com.awooga.ProfilesPaperGui.chestgui.ChestGui;
import com.awooga.ProfilesPaperGui.dao.ProfileEntity;
import com.awooga.ProfilesPaperGui.fsm.events.PlayerUUIDOverrideEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProfilesPaperGuiEventListener implements Listener {

	ProfilesPaperGui plugin;
	public ProfilesPaperGuiEventListener(ProfilesPaperGui main) {
		plugin = main;
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onUUIDOverrideEvent(PlayerUUIDOverrideEvent ev) {
		Bukkit.getLogger().info("Got PlayerUUIDOverrideEvent");
		this.plugin.playerProfilesDAO.storeUuidOverride(ev.getOriginalUuid(), ev.getCurrentUuid());
		Player player = ev.getPlayer();
		if(this.plugin.playerProfilesDAO.isProfileIdBrandNew(ev.getCurrentUuid())) {
			Bukkit.getLogger().info("Running new profile hooks...");
			this.plugin.playerProfilesDAO.removeBrandNewProfileId(ev.getCurrentUuid());
			this.plugin.hookExecutionHelper.executeHooks(player, HookExecutionHelper.PROFILE_CREATE_HOOK);
		}
		this.plugin.hookExecutionHelper.executeHooks(player, HookExecutionHelper.PROFILE_SWITCH_HOOK);
	}
	// MUST be lowest priority to run before other cleanup hooks of other plugins, since this call relies on the data
	// that those other plugins might clean up
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent ev) {
		boolean preferCachedPlaceholders = plugin.getConfig().getBoolean("options.preferCachedPlaceholders", false);
		if(!preferCachedPlaceholders) {
			return; // not necessary if we won't actually use the cache
		}

		Player player = ev.getPlayer();
		UUID genuineUuid = this.plugin.playerProfilesDAO.getGenuineUUID(player);
		if(player.getUniqueId().equals(genuineUuid)) {
			return;
		}

		List<ProfileEntity> profiles = this.plugin.playerProfilesDAO.getProfileEntitiesByGenuineUUID(genuineUuid);
		Optional<ProfileEntity> maybeProfileEntity = profiles.stream().filter(p -> player.getUniqueId().equals(p.getProfileUuid())).findAny();

		if(!maybeProfileEntity.isPresent()) {
			return;
        }

		ProfileEntity profileEntity = maybeProfileEntity.get();

		ChestGui<Object> chestGui = plugin.chestGuiGenerator.createNewGui("gui.profileSelectorMain", null);

		profileEntity = profileEntity.toBuilder()
			.cachedPlaceholderTitle(chestGui.getText(player, "slotCreated.title"))
			.cachedPlaceholderBody(chestGui.getText(player, "slotCreated.body"))
		.build();

		this.plugin.playerProfilesDAO.save(profileEntity);
	}

	@EventHandler
	public void onPlayerDisconnectEvent(PlayerQuitEvent ev) {
		this.plugin.playerProfilesDAO.onUserDisconnect(ev.getPlayer());
	}
}
