package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatPlayerDeaths extends Stat {

    public StatPlayerDeaths() {
        updateEntryAfterValue = 1;
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        String cause = e.getEntity().getLastDamageCause() == null ? null : e.getEntity().getLastDamageCause().getCause().toString();
        increaseValue(e.getEntity(), 1, cause);
    }

}
