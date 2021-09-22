package com.awooga.ProfilesPaperGui;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HookExecutionHelper {
	public static final String PROFILE_CREATE_HOOK = "onCreateProfile";
	public static final String PROFILE_SWITCH_HOOK = "onSwitchProfile";
	public static final String PROFILE_DELETE_HOOK = "onDeleteProfile";

	private final ProfilesPaperGui plugin;
	public HookExecutionHelper(ProfilesPaperGui main) {
		plugin = main;
	}

	public void executeHooks(Player player, String hookName) {
		List<String> commands = plugin.getConfig().getStringList("hooks."+hookName);
		for(String command : commands) {
			Bukkit.getLogger().info("Running command (unexpanded): "+command);

			String expandedCommand = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command);
			Bukkit.getLogger().info("Running command (expanded): "+expandedCommand);

			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			Bukkit.dispatchCommand(console, expandedCommand);
		}
	}
}
