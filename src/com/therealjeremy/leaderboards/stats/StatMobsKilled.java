package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class StatMobsKilled extends Stat implements Listener {

    public StatMobsKilled(Main plugin) {
        super(plugin);
        updateEntryAfterValue = 25;
        updateEntryAfterSeconds = 300;
    }

    @EventHandler
    public void killMobEvent(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            return;
        }
        if (e.getEntity().getKiller() != null) {
            increaseValue(e.getEntity().getKiller(), 1);
        }
    }

}
