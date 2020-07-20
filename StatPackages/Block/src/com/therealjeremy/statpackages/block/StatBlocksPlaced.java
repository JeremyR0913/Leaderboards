package com.therealjeremy.statpackages.block;

import com.therealjeremy.leaderboards.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class StatBlocksPlaced extends Stat {

    public StatBlocksPlaced() {
        updateEntryAfterValue = 1000;
        updateEntryAfterSeconds = 300;
    }

    @EventHandler
    public void mineBlockEvent(BlockPlaceEvent e){
        increaseValue(e.getPlayer(), 1, e.getBlock().getType().toString());
    }

}
