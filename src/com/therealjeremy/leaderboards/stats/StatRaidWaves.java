package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.raid.RaidSpawnWaveEvent;

public class StatRaidWaves extends Stat {

    public StatRaidWaves(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void raidWave(RaidSpawnWaveEvent e){
        Location location = e.getRaid().getLocation();
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getLocation().getWorld().equals(location.getWorld()) && player.getLocation().distance(location) <= 50){
                increaseValue(player, 1, "level_" + e.getRaid().getBadOmenLevel());
            }
        }
    }

}
