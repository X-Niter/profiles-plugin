package com.awooga.ProfilesWaterfall;

import com.awooga.ProfilesWaterfall.dao.ProfileDAO;
import com.awooga.ProfilesWaterfall.dao.impl.ProfileDAOImpl;
import com.awooga.profiles.ProfilesConstants;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class ProfilesWaterfall extends Plugin implements Listener {

    @Getter
    public static ProfilesWaterfall instance;

    public ProfilesWaterfallEventListener listener;

    public SetUUIDCommand setUUIDCommand;

    public UUIDSetterHelper uuidSetterHelper;

    public ProfileDAOImpl profileDAOImpl;

    @Override
    public void onEnable() {

        instance = this;
        listener = new ProfilesWaterfallEventListener(this);
        setUUIDCommand = new SetUUIDCommand();
        uuidSetterHelper = new UUIDSetterHelper(this);
        profileDAOImpl = new ProfileDAOImpl(this);

        RegisterChannels();
        RegisterListeners();
    }

    public void RegisterChannels() {
        getProxy().registerChannel(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS);
        getProxy().registerChannel(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.length() - 1));
        getProxy().registerChannel(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS);
        getProxy().registerChannel(ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.length() - 1));

    }

    public void RegisterListeners() {
        PluginManager manager = getProxy().getPluginManager();
        manager.registerListener(this, listener);
        manager.registerCommand(this, setUUIDCommand);
    }

    public TaskScheduler getScheduler() {
        return this.getProxy().getScheduler();
    }

    @Override
    public void onDisable() {
        //Spigot or Paper already runs it's own disable logic so we can keep this empty
    }
}
