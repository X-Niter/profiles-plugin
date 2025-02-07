package com.awooga.ProfilesWaterfall;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class SetUUIDCommand extends Command {

    SetUUIDCommand() {
        super("setuuid");
    }

    private static final ProfilesWaterfall plugin = ProfilesWaterfall.getInstance();

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "setuuid only works for players"));
            return;
        }
        if(strings.length == 0 || "".equals(strings[0])) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /setuuid [uuid]"));
            return;
        }

        if(!commandSender.hasPermission("profileswaterfall.admin.setuuid")) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Missing permission to use /setuuid: profileswaterfall.admin.setuuid"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Hello World!" + player.toString()));

        UUID newUuid = UUID.fromString(strings[0]);

        plugin.uuidSetterHelper.setUuid(player, newUuid);
    }
}
