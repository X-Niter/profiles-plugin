package com.awooga.ProfilesWaterfall;

import com.awooga.ProfilesWaterfall.dao.impl.ProfileDAOImpl;
import com.awooga.profiles.ProfilesConstants;
import com.awooga.ProfilesWaterfall.dao.ProfileDAO;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;
import java.util.UUID;

public class ProfilesWaterfallEventListener implements Listener {

    ProfilesWaterfall plugin;
    public ProfilesWaterfallEventListener(ProfilesWaterfall main) {
        plugin = main;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent ev) {
        plugin.profileDAOImpl.onUserDisconnect(ev.getPlayer());
    }

    @EventHandler
    public void onLogin(LoginEvent ev) {
        /*
        PendingConnection conn = ev.getConnection(); //ev.getPlayer().getPendingConnection()
        String s = conn.getUniqueId().toString();
        System.out.println("USer connecting with uuid: "+s);
        if(s.equals("2c8e8bb8-1a58-45b9-b023-e060a0296e15")) {
            System.out.println("Setting uuid to notch...");
            UUID newUuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");

            try {
                FieldUtils.writeField(conn, "uniqueId", newUuid, true);
            } catch (IllegalAccessException e) {
                System.out.println("Couldn't set field uniqueid: " + e.toString());
                e.printStackTrace();
            }
        }
        */
    }

    @EventHandler
    public void onServerConnected(ServerSwitchEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        UUID originalUuid = plugin.profileDAOImpl.getOriginalUUID(player).get();

        if(plugin.profileDAOImpl.getUserTargetServer(player).isPresent()) {
            plugin.uuidSetterHelper.sendUserToOriginalServer(player, plugin.profileDAOImpl.getUserTargetServer(player).get());
            return;
        }

        if(plugin.profileDAOImpl.getRealUUID(originalUuid).isPresent()) {
            UUID overrideUuid = plugin.profileDAOImpl.getRealUUID(originalUuid).get();
            plugin.uuidSetterHelper.setUuidBeforeLogin(player.getPendingConnection(), overrideUuid);
        }
        plugin.uuidSetterHelper.notifyServerOfUuidOverride(player);
    }

    @SneakyThrows
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        System.out.println("Got plugin message event: "+event+" - "+event.getTag());
        if (
            !event.getTag().equals(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS) &&
            !event.getTag().equals(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.length() - 1))
        ) {
            return;
        }
        System.out.println("BUNGEE_CHANNEL_NAME_FOR_REQUESTS received a byte stream...");
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();

        if(
            ProfilesConstants.SWITCH_PLAYER_TO_NEW_PROFILE.equals(channel) ||
            ProfilesConstants.SWITCH_PLAYER_TO_NEW_PROFILE.substring(0, ProfilesConstants.SWITCH_PLAYER_TO_NEW_PROFILE.length() - 1).equals(channel)
        ) {
            String profileUuidString = in.readUTF();
            UUID profileUuid = UUID.fromString(profileUuidString);
            //System.out.println("Got profile UUID string: "+profileUuid);
            /*
            String onlinePlayers = ProxyServer.getInstance().getPlayers().stream()
                .map(p -> p.getUniqueId()+" - "+p.getDisplayName())
                .collect(Collectors.joining(","));
            System.out.println("Got online players: " + onlinePlayers);
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(genuineUuid);
             */
            if(!(event.getReceiver() instanceof ProxiedPlayer)) {
                System.out.println("Cannot process profile switch event -- socket is not a player instance");
            }
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

            if(player == null) {
                throw new Exception("SWITCH_PLAYER_TO_NEW_PROFILE command got a uuid that doesn't refer to an online player");
            }
            plugin.uuidSetterHelper.setUuid(player, profileUuid);
        }
    }
}