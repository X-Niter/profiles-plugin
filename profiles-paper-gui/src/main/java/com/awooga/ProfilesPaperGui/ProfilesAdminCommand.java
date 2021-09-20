package com.awooga.ProfilesPaperGui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProfilesAdminCommand implements CommandExecutor {

	ProfilesPaperGui plugin;
	public ProfilesAdminCommand(ProfilesPaperGui main) {
		plugin = main;
	}


	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
		if(!(commandSender instanceof Player)) {
			commandSender.sendMessage(ChatColor.RED + "/profilesadmin only works for players");
			return false;
		}

		if(!commandSender.hasPermission("profiles.admin")) {
			commandSender.sendMessage(ChatColor.RED + "Missing permission to use /profilesadmin: profiles.admin");
			return false;
		}

		String subcommand = args.length != 0 ? args[0] : "";

		if("reload".equals(subcommand)) {
			plugin.reload(commandSender);
			commandSender.sendMessage(ChatColor.GREEN + "Configuration has been reloaded");
		} else {
			commandSender.sendMessage(ChatColor.RED + "Unrecognized subcommand: "+subcommand);
		}

		return true;
	}
}
