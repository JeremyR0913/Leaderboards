package com.therealjeremy.leaderboards;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.UUID;

public class Main extends JavaPlugin {

    /*
    This is where spigot unregisters the plugin and disables it.
     */
    public void onDisable() {
        statManager.terminate();
    }

    private Main plugin;
    private static Connection sqlConnection;


    /*
    This is where the plugin is registered by spigot and starts running.
     */
    public void onEnable() {
        plugin = this;
        try {
            registerDatabase();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        registerManagers();
        registerEvents();
        registerCommands();
    }

    /*
    Where I register commands.
     */
    private void registerCommands(){
        plugin.getCommand("leaderboard").setExecutor(new LeaderboardCMD(plugin));
        plugin.getCommand("jsql").setExecutor(new SQLCMD());
    }

    /*
    Where I register events.
     */
    private void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new JoinQuitEvents(plugin), plugin);
        pm.registerEvents(new ChunkEvents(plugin), plugin);
    }

    private StatManager statManager;

    /*
    Where I registered managers. If this were a large plugin like JCore I would have many of these.
     */
    private void registerManagers() {
        statManager = new StatManager(plugin);
    }

    /*
    Where my database is initialized.
     */
    private void registerDatabase() throws SQLException, ClassNotFoundException {
        if (!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }
        String sqlUrl = "jdbc:sqlite:plugins/Leaderboards/database.db";
        if (sqlConnection != null && !sqlConnection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (sqlConnection != null && !sqlConnection.isClosed()) {
                return;
            }
            Class.forName("org.sqlite.JDBC");
            sqlConnection = DriverManager.getConnection(sqlUrl);
            log("Connection to database established.");
        }
        Statement statement = sqlConnection.createStatement();
        statement.execute("create table if not exists playerNames(id varchar PRIMARY KEY, name varchar)");
        statement.close();
    }

    /*
    Utility method for executing statements to the SQL database.
     */
    public void executeImmediately(String string) {
        Statement statement = null;
        try {
            statement = getSqlConnection().createStatement();
            statement.execute(string);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void log(Object o) {
        System.out.println("[Leaderboards] " + o);
    }

    public static Connection getSqlConnection() {
        return sqlConnection;
    }

    public StatManager getStatManager() {
        return statManager;
    }

    public static DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /*
    I store player names/UUIDs in a table in the database because it
    is very resource demanding to get the name/UUID of an offline player
    using regular Spigot methods.
     */
    public static String playerNameFromId(UUID uuid) {
        Statement statement = null;
        String name = null;
        try {
            statement = getSqlConnection().createStatement();
            ResultSet set = statement.executeQuery("select name from playerNames where id='" + uuid.toString() + "'");
            if (set.next()) {
                name = set.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return name;
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
