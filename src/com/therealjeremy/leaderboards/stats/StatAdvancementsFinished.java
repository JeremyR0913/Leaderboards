package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class StatAdvancementsFinished extends Stat {

    public StatAdvancementsFinished() {
        updateEntryAfterValue = 1;
    }

    @EventHandler
    public void advancementDone(PlayerAdvancementDoneEvent e){
        increaseValue(e.getPlayer(), 1, e.getAdvancement().toString());
    }

}
