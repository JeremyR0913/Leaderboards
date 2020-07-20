package com.therealjeremy.leaderboards.events;

import com.therealjeremy.leaderboards.Leaderboard;
import com.therealjeremy.leaderboards.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkEvents implements Listener {

    private Main plugin;

    public ChunkEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent e){
        for (Leaderboard leaderboard : plugin.getStatManager().leaderboards()){
            if (leaderboard.getLocation().getChunk().equals(e.getChunk())){
                leaderboard.terminate();
            }
        }
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent e){
        for (Leaderboard leaderboard : plugin.getStatManager().leaderboards()){
            if (leaderboard.getLocation().getChunk().equals(e.getChunk())){
                leaderboard.initialize();
            }
        }
    }

}
