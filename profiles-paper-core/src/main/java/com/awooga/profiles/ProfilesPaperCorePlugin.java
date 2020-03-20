package com.awooga.profiles;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public final class ProfilesPaperCorePlugin extends JavaPlugin {

    @Inject
    ProfilesPaperCoreMessageListener profilesPaperCoreMessageListener;

    @Inject
    ProfilesPaperCoreSDK sdk;

    @Override
    public void onEnable() {
        if(!this.checkIfBungee()){ return; }
        // Plugin startup logic
        ProfilesPaperCoreModule module = new ProfilesPaperCoreModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);
        // we register the incoming channel
        Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel( this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS, this.profilesPaperCoreMessageListener);
        messenger.registerOutgoingPluginChannel(this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS);
        getServer().getPluginManager().registerEvents(this.profilesPaperCoreMessageListener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // we check like that if the specified server is BungeeCord.
    private boolean checkIfBungee()
    {
        // we check if the server is Spigot/Paper (because of the spigot.yml file)
        if ( !getServer().getVersion().contains( "Spigot" ) && !getServer().getVersion().contains( "Paper" ) )
        {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return false;
        }
        if ( getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean( "settings.bungeecord" ) )
        {
            getLogger().severe( "This server is not using BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return false;
        }
        return true;
    }
}
