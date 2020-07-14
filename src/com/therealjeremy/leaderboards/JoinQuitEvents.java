package com.therealjeremy.leaderboards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JoinQuitEvents implements Listener {

    private Main plugin;

    public JoinQuitEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent e){
        Player player = e.getPlayer();
        try {
            Statement statement = plugin.getSqlConnection().createStatement();
            ResultSet set = statement.executeQuery("select * from playerNames where id='" + player.getUniqueId() + "'");
            if (!set.next()){
                plugin.executeImmediately("insert into playerNames values ('" + player.getUniqueId() + "', '" + player.getName() + "')");
            }else{
                plugin.executeImmediately("update playerNames set name='" + player.getName() + "' where id='" + player.getUniqueId() + "'");
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent e){
    }

}
