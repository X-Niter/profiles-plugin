package com.awooga.ProfilesPaperGui.sql;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

public class DbManager {

    ProfilesPaperGui plugin;

    //manage the connections

    private final DbConnection gradeConnection;

    public DbManager(ProfilesPaperGui main) {
        plugin = main;

        FileConfiguration config = main.getConfig();

        this.gradeConnection = new DbConnection(new DbCredentials(
                config.getString("mysql.host"),
                config.getString("mysql.user"),
                config.getString("mysql.password"),
                config.getString("mysql.DbName"),
                config.getInt("mysql.port")));
    }

    public DbConnection getGradeConnection() {
        return gradeConnection;
    }


    public void close(){

        try {
            this.gradeConnection.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("SQL Connection failed, wrong credentials or Database is offline? StackTrace below!");
            e.printStackTrace();
        }
    }

}