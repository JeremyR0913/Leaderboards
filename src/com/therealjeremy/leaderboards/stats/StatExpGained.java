package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class StatExpGained extends Stat {

    public StatExpGained(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void gainExp(PlayerExpChangeEvent e){
        if (e.getAmount() > 0){
            increaseValue(e.getPlayer(), e.getAmount(), e.getPlayer().getWorld().toString());
        }
    }

}
