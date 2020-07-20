package com.therealjeremy.statpackages.raid;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.raid.RaidTriggerEvent;

public class StatTriggerRaid extends Stat {

    public StatTriggerRaid() {
        updateEntryAfterValue = 1;
    }

    @EventHandler
    public void triggerRaid(RaidTriggerEvent e){
        increaseValue(e.getPlayer(), 1, "level_" + e.getRaid().getBadOmenLevel());
    }

}
