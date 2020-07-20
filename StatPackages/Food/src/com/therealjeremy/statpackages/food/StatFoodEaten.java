package com.therealjeremy.statpackages.food;

import com.therealjeremy.leaderboards.Main;
import com.therealjeremy.leaderboards.Stat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class StatFoodEaten extends Stat {

    public StatFoodEaten() {
        updateEntryAfterValue = 1;
    }

    @EventHandler
    public void eatFood(PlayerItemConsumeEvent e){
        if (isFood(e.getItem().getType())){
            Main.log(e.getPlayer().getName() + " ate " + e.getItem().getType());
            increaseValue(e.getPlayer(),1, e.getItem().getType().toString());
        }
    }

    private boolean isFood(Material material){
        switch (material){
            case APPLE:
            case BAKED_POTATO:
            case BEETROOT:
            case BEETROOT_SOUP:
            case BREAD:
            case CAKE:
            case CARROT:
            case CHORUS_FRUIT:
            case COOKED_BEEF:
            case COOKED_CHICKEN:
            case COOKED_COD:
            case COOKED_MUTTON:
            case COOKED_PORKCHOP:
            case COOKED_RABBIT:
            case COOKED_SALMON:
            case COOKIE:
            case DRIED_KELP:
            case ENCHANTED_GOLDEN_APPLE:
            case GOLDEN_APPLE:
            case GOLDEN_CARROT:
            case HONEY_BOTTLE:
            case MELON_SLICE:
            case MUSHROOM_STEM:
            case POISONOUS_POTATO:
            case POTATO:
            case PUFFERFISH:
            case PUMPKIN_PIE:
            case RABBIT_STEW:
            case BEEF:
            case CHICKEN:
            case COD:
            case MUTTON:
            case PORKCHOP:
            case RABBIT:
            case SALMON:
            case ROTTEN_FLESH:
            case SPIDER_EYE:
            case SUSPICIOUS_STEW:
            case SWEET_BERRIES:
            case TROPICAL_FISH:
                return true;
            default:
                return false;
        }
    }

}
