package com.therealjeremy.statpackages.block;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class StatBlocksMined extends Stat {

    public StatBlocksMined() {
        updateEntryAfterValue = 1000;
        updateEntryAfterSeconds = 300;
    }

    @EventHandler
    public void mineBlockEvent(BlockBreakEvent e) {
        increaseValue(e.getPlayer(), 1, e.getBlock().getType().toString());
    }

}
