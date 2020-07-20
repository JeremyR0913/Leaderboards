package com.therealjeremy.statpackages.fighting;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatPlayersKilled extends Stat {

    public StatPlayersKilled() {
        updateEntryAfterValue = 1;
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        if (e.getEntity().getKiller() != null){
            Player killer = e.getEntity().getKiller();
            Player player = e.getEntity();
            increaseValue(killer, 1, player.getUniqueId().toString());
        }
    }

}
