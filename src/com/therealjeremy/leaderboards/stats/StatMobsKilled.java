package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatMobsKilled extends Stat {

    public StatMobsKilled(Main plugin) {
        super(plugin);
        updateEntryAfterValue = 1;
        updateEntryAfterSeconds = 1000000;
    }

    @EventHandler
    public void killMobEvent(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            return;
        }
        if (e.getEntity().getKiller() != null) {
            increaseValue(e.getEntity().getKiller(), 1, e.getEntity().getType().toString());
        }
    }

}
