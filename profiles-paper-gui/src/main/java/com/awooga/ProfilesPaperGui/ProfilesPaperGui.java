package com.awooga.ProfilesPaperGui;

import com.awooga.ProfilesPaperGui.chestgui.ChestGuiGenerator;
import com.awooga.ProfilesPaperGui.dao.impl.PlayerProfilesDAOImpl;
import com.awooga.ProfilesPaperGui.fsm.core.CoreMessageListener;
import com.awooga.ProfilesPaperGui.sql.DbManager;
import com.awooga.ProfilesPaperGui.util.ConsoleOutput;
import com.awooga.ProfilesPaperGui.util.ConsoleStringUtils;
import com.awooga.profiles.ProfilesConstants;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ProfilesPaperGui extends JavaPlugin {

    private static ProfilesPaperGui instance;
    public ProfilesCommand2 profilesCommand2;
    public DbManager databaseManager;
    public ConsoleOutput consoleOutput;
    public PlayerProfilesDAOImpl playerProfilesDAO;
    public HookExecutionHelper hookExecutionHelper;
    public ChestGuiGenerator chestGuiGenerator;
    public final CoreMessageListener coreMessageListener = new CoreMessageListener(this);

    public static ProfilesPaperGui getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if(!this.checkIfBungee()){ return; }

        databaseManager = new DbManager(this);
        playerProfilesDAO = new PlayerProfilesDAOImpl(this);
        hookExecutionHelper = new HookExecutionHelper(this);
        chestGuiGenerator = new ChestGuiGenerator(this);

        saveDefaultConfig();

        consoleOutput = new ConsoleOutput(this);
        consoleOutput.setColors(true);
        consoleOutput.setPrefix(Objects.requireNonNull(ConsoleStringUtils.color("&e[&bProfiles&e] &r")));

        registerMessenger();
        registerListeners();

        checkDependencies();

        playerProfilesDAO.applyMigrations();

        profilesCommand2 = new ProfilesCommand2(this);
        getCommand("profiles").setExecutor(profilesCommand2);
        getCommand("profilesadmin").setExecutor(new ProfilesAdminCommand(this));
        profilesCommand2.onEnable();

    }

    public void registerMessenger() {
        Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel( this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS, coreMessageListener);
        messenger.registerIncomingPluginChannel( this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_NOTIFICATIONS.length() - 1), this.coreMessageListener);
        messenger.registerOutgoingPluginChannel(this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS);
        messenger.registerOutgoingPluginChannel(this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.substring(0, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS.length() - 1));

    }

    public void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new ProfilesCommand2(this), this);
        pluginManager.registerEvents(new ProfilesPaperGuiEventListener(this), this);
        pluginManager.registerEvents(new CoreMessageListener(this), this);
    }

    public void reload(CommandSender sender) {

        checkDependencies();

        this.getConfig();

        sender.sendMessage("Profiles successfully reloaded");
    }

    public void checkDependencies() {
        setupPlaceholderAPI();
    }

    public void setupPlaceholderAPI() {
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ProfilesPlaceholderExpansion(this).register();
        } else {
            getLogger().log(Level.SEVERE, "Couldn't initialize ProfilesPaperGuiPlugin -- missing PlaceholderAPI plugin");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        databaseManager.close();
        Bukkit.getPluginManager().disablePlugin(this);
    }

    // Checking spigot.yml config to make sure Bungee is enabled
    private boolean checkIfBungee()
    {
        if (getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean( "settings.bungeecord")) {
            getLogger().severe( "This server is not using BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return false;
        }
        return true;
    }

    public void switchPlayerToProfile(Player player, UUID profileUuid) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( ProfilesConstants.SWITCH_PLAYER_TO_NEW_PROFILE );
        out.writeUTF( profileUuid.toString() );
        player.sendPluginMessage( this, ProfilesConstants.BUNGEE_CHANNEL_NAME_FOR_REQUESTS, out.toByteArray() ); // Send to Bungee
    }

    public DbManager getDatabaseManager() {
        return databaseManager;
    }

}
