package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatTimePlayed extends Stat {

    private Map<Player, Long> playerMap = new HashMap<>();
    private Map<Player, AFKInfo> afkMap = new HashMap<>();
    private BukkitTask task;

    // TODO
    // Could be optimized to use less memory
    // Only use map to store data for players who joined within update span

    public StatTimePlayed(Main plugin) {
        super(plugin);
        updateEntryAfterSeconds = 300;
        updateEntryAfterValue = 0;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {

            @Override
            public void run() {
                for (Player player : playerMap.keySet()) {
                    long timeJoined = playerMap.get(player);
                    AFKInfo afkInfo = afkMap.get(player);
                    float pitch = afkInfo.pitch;
                    float yaw = afkInfo.yaw;
                    if (pitch != player.getLocation().getPitch() || yaw != player.getLocation().getYaw()){
                        increaseValue(player, (int) (System.currentTimeMillis() - timeJoined), player.getWorld().getName());
                    }
                    afkInfo.pitch = player.getLocation().getPitch();
                    afkInfo.yaw = player.getLocation().getYaw();
                    playerMap.put(player, System.currentTimeMillis());
                    afkMap.put(player, afkInfo);
                }
            }

        }, 60 * 20L, 60 * 20L);
    }

    @Override
    public void initialize(Connection sqlConnection) throws SQLException {
        super.initialize(sqlConnection);
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerMap.put(player, System.currentTimeMillis());
            afkMap.put(player, new AFKInfo(player.getLocation().getPitch(), player.getLocation().getYaw()));
        }
    }

    @Override
    public void terminate() {
        for (Player player : playerMap.keySet()) {
            long timeJoined = playerMap.get(player);
            AFKInfo afkInfo = afkMap.get(player);
            float pitch = afkInfo.pitch;
            float yaw = afkInfo.yaw;
            if (pitch != player.getLocation().getPitch() || yaw != player.getLocation().getYaw()){
                increaseValue(player, (int) (System.currentTimeMillis() - timeJoined), player.getWorld().getName());
            }
        }
        playerMap.clear();
        afkMap.clear();
        task.cancel();
        super.terminate();
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        playerMap.put(player, System.currentTimeMillis());
        afkMap.put(player, new AFKInfo(player.getLocation().getPitch(), player.getLocation().getYaw()));
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        long timeJoined = playerMap.get(player);
        AFKInfo afkInfo = afkMap.get(player);
        float pitch = afkInfo.pitch;
        float yaw = afkInfo.yaw;
        if (pitch != player.getLocation().getPitch() || yaw != player.getLocation().getYaw()){
            increaseValue(player, (int) (System.currentTimeMillis() - timeJoined), player.getWorld().getName());
        }
        playerMap.remove(player);
        afkMap.remove(player);
    }

    @Override
    public String format(double value) {
        String format;
        int seconds = (int) value / 1000;
        if (seconds > 86400) {
            format = Main.decimalFormat.format((double) seconds / 86400.0) + " days";
        } else if (seconds > 3600) {
            format = Main.decimalFormat.format((double) seconds / 3600.0) + " hours";
        } else {
            format = Main.decimalFormat.format((double) seconds / 60.0) + " minutes";
        }
        return format;
    }

    private class AFKInfo {

        public float pitch;
        public float yaw;

        public AFKInfo(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }

    }

}
