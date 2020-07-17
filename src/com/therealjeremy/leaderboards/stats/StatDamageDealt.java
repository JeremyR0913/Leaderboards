package com.therealjeremy.leaderboards.stats;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class StatDamageDealt extends Stat {

    public StatDamageDealt(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void dealDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            increaseValue(player, (int) (e.getFinalDamage() * 10), e.getCause().toString());
        }else if (e.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Player){
                Player player = (Player) arrow.getShooter();
                increaseValue(player, (int) (e.getFinalDamage() * 10), e.getCause().toString());
            }
        }
    }

    /*
    Converts to hearts.
     */
    @Override
    public String format(double value) {
        value = value / 20;
        value = value - (value % 0.5);
        return super.format(value);
    }

}
