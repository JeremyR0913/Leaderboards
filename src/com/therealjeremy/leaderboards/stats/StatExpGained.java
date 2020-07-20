package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class StatExpGained extends Stat {

    @EventHandler
    public void gainExp(PlayerExpChangeEvent e){
        if (e.getAmount() > 0){
            increaseValue(e.getPlayer(), e.getAmount(), e.getPlayer().getWorld().toString());
        }
    }

}
