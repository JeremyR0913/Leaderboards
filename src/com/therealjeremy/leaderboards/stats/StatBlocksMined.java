package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class StatBlocksMined extends Stat {

    public StatBlocksMined(Main plugin) {
        super(plugin);
        updateEntryAfterValue = 1000;
        updateEntryAfterSeconds = 300;
    }

    @EventHandler
    public void mineBlockEvent(BlockBreakEvent e){
        increaseValue(e.getPlayer(), 1);
    }

}
