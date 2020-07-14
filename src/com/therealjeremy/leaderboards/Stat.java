package com.therealjeremy.leaderboards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Stat implements Listener {

    /*
    All of the specific statistics extend this class. This class is basically
    an outline for every statistic created, and it utilizes default methods that
    specific statistics don't need to include unless it is desired to override the method.
     */

    // Table columns:
    // time
    // id
    // value

    private Main plugin;

    public Stat(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /*
    Updates the database and removes the player from RAM storage when
    the player quits the server.
     */
    @EventHandler
    public void quitEvent(PlayerQuitEvent e) {
        updateEntry(e.getPlayer());
        pendingValuesMap.remove(e.getPlayer());
    }

    private Connection sqlConnection;
    private Map<Player, Integer> pendingValuesMap = new ConcurrentHashMap<>();
    private BukkitTask task;
    public int updateEntryAfterValue = 50;
    public int updateEntryAfterSeconds = 60;

    /*
    Executed from StatManager.java.
    Initializes the stat, starts timer that updates database periodically.
     */
    public void initialize(Connection sqlConnection) throws SQLException {
        this.sqlConnection = sqlConnection;
        Statement statement = sqlConnection.createStatement();
        statement.execute("create table if not exists " + getTableName() + "(time float(24), id varchar, value int)");
        statement.close();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {

            private int counter = (int) (new Random().nextDouble() * 30);

            @Override
            public void run() {
                Iterator<Player> iterator = pendingValuesMap.keySet().iterator();
                while (iterator.hasNext()){
                    Player player = iterator.next();
                    if (pendingValuesMap.get(player) >= updateEntryAfterValue) {
                        updateEntry(player);
                        pendingValuesMap.remove(player);
                    }
                }
                if (updateEntryAfterSeconds > 0 && counter % updateEntryAfterSeconds == 0) {
                    for (Player player : pendingValuesMap.keySet()) {
                        updateEntry(player);
                    }
                    pendingValuesMap.clear();
                }
                counter++;
            }

        }, 20L, 20L);
        Main.log("Initialized Stat: " + getIdentifier());
    }

    /*
    Runs when the plugin is disabled (server stop/reload)
    Updates database for all players.
     */
    public void terminate() {
        for (Player player : pendingValuesMap.keySet()) {
            updateEntry(player);
        }
        pendingValuesMap.clear();
        task.cancel();
    }

    public String getIdentifier() {
        return this.getClass().getSimpleName();
    }

    public String getTableName() {
        return getIdentifier() + "_table";
    }

    /*
    Executes query that returns entries for the leaderboard depending on specified time and size.
     */
    public List<LeaderboardEntry> getLeaderboard(int size, long time) {
        List<LeaderboardEntry> list = new ArrayList<>();
        Statement statement = null;
        try {
            statement = sqlConnection.createStatement();
            ResultSet set = statement.executeQuery("select id, sum(value) vsum from " + getTableName() + " where time > " + time + " group by id order by vsum desc limit " + size);
            while (set.next()) {
                list.add(new LeaderboardEntry(set.getString(1), set.getInt(2)));
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
        return list;
    }

    /*
    Returns the total value of the player's stat.
     */
    public int getValue(Player player){
        int n = 0;
        Statement statement = null;
        try {
            statement = sqlConnection.createStatement();
            ResultSet set = statement.executeQuery("select sum(value) from " + getTableName() + " where id='" + player.getUniqueId().toString() + "'");
            if (set.next()){
                n = set.getInt(1);
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
        return n;
    }

    /*
    Updates the value in the Map. *Does not update database*
     */
    public void increaseValue(Player player, int amount) {
        if (pendingValuesMap.containsKey(player)) {
            int n = pendingValuesMap.get(player);
            pendingValuesMap.put(player, n + amount);
        } else {
            pendingValuesMap.put(player, amount);
        }
    }

    /*
    Formatting the value for reading on the in-game leaderboard.
     */
    public String format(int value){
        return Main.decimalFormat.format(value);
    }

    /*
    Updates database with player's values.
     */
    public void updateEntry(Player player) {
        plugin.executeImmediately("insert into " + getTableName() + " (id, value, time) values ('" + player.getUniqueId().toString() + "', " + pendingValuesMap.get(player) + ", " + System.currentTimeMillis() + ")");
    }

}