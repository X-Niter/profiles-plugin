package com.awooga.ProfilesPaperGui.sql;

public class DbCredentials {

    //setting up db credentials

    private final String host;
    private final String user;
    private final String pass;
    private final String dbName;
    private final int port;

    protected void start(){

    }


    public DbCredentials(String host, String user, String pass, String dbName, int port) {

        this.host = host;
        this.user = user;
        this.pass = pass;
        this.dbName = dbName;
        this.port = port;
    }

    public String toURI() {

        final StringBuilder sb = new StringBuilder();

        sb.append("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dbName);

        return sb.toString();

    }

    public String getUser() {

        return user;
    }


    public String getPass() {

        return pass;
    }

    public String getHost() {

        return host;
    }


    public String getDbName() {

        return dbName;
    }

    public int getPort() {

        return port;
    }

}