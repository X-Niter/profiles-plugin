package com.awooga.ProfilesPaperGui.sql;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    //setting up connection parameters

    private final DbCredentials dbCredentials;
    private Connection connection;




    public DbConnection(DbCredentials dbCredentials){

        this.dbCredentials = dbCredentials;
        this.connect();

    }

    private void connect(){

        try {

            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection(this.dbCredentials.toURI(), this.dbCredentials.getUser(), this.dbCredentials.getPass());

            Bukkit.getLogger().info("Successfully connected to SQL Database");

        } catch (SQLException | NullPointerException | ClassNotFoundException e) {
            Bukkit.getLogger().severe("SQL Connection failed, wrong credentials or Database is offline? StackTrace below!");
            e.printStackTrace();
        }

    }

    protected void close() throws SQLException{

        if(this.connection != null){

            if(!this.connection.isClosed()){

                this.connection.close();
            }
        }

    }

    public Connection getConnection() throws SQLException{

        if(this.connection != null){

            if(!this.connection.isClosed()){

                return this.connection;
            }
        }

        connect();
        return this.connection;

    }
}