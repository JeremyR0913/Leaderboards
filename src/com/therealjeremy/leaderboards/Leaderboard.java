package com.therealjeremy.leaderboards;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Leaderboard {

    private File file;
    private Main plugin;

    public Leaderboard(Main plugin, File file, Stat stat) {
        this.plugin = plugin;
        this.file = file;
        this.stat = stat;
    }

    /*
    Constructor used when creating new leaderboards. Registers defaults.
     */
    public Leaderboard(Main plugin, String identifier, Location location, Stat stat) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder() + File.separator + "leaderboards", identifier + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.location = location;
        this.stat = stat;
        this.title = identifier;
        this.size = 5;
        this.recentSeconds = -1;
        this.refreshSeconds = 60;
        saveFile();
    }

    private String identifier;
    private String title;
    private String category;
    private int size;
    private Location location;
    private int recentSeconds;
    private int refreshSeconds;
    private Stat stat;
    private BukkitTask task;
    private Map<Integer, ArmorStand> armorStandMap = new HashMap<>();
    private String format = "&e{name} &f{value}";

    /*
    Executed from StatManager.java.
    Loads info from leaderboard file.
    Variables:
        title = title of armorstand
        size = how many players to show in leaderboard
        recentSeconds = how recent of values you want to look at, I.E. if you want a leaderboard
            that looks at the last 24 hours this would be 86400
        refreshSeconds = how often the database is queried and the armorstands update.
        location = location of leaderboard
        task = task for updating armorstands.
        space = distance (height) between armor stands.
        armorStandMap = Map (runtime storage) referencing armorstands.
     */
    public void initialize() {
        identifier = file.getName().substring(0, file.getName().length() - 4).toLowerCase();
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        title = fileConfig.getString("title");
        category = fileConfig.getString("category");
        size = fileConfig.getInt("size");
        recentSeconds = fileConfig.getInt("recency-seconds");
        refreshSeconds = fileConfig.getInt("refresh-seconds");
        location = new Location(Bukkit.getWorld(fileConfig.getString("location.world")),
                fileConfig.getDouble("location.x"),
                fileConfig.getDouble("location.y"),
                fileConfig.getDouble("location.z"));
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {

            private double space = 0.4;

            @Override
            public void run() {
                if (armorStandMap.isEmpty()) {
                    Location l = location.clone();
                    l.add(0, space * size - 1.7, 0);
                    for (int i = 0; i < size + 1; i++) {
                        ArmorStand as = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
                        as.setCustomName(Main.color("&e-"));
                        as.setCustomNameVisible(true);
                        as.setGravity(false);
                        as.setVisible(false);
                        armorStandMap.put(i, as);
                        l.add(0, -space, 0);
                    }
                }
                armorStandMap.get(0).setCustomName(Main.color(title));
                int i = 1;
                for (LeaderboardEntry lbe : stat.getLeaderboard(size, recentSeconds < 0 ? 0 : (System.currentTimeMillis() - (recentSeconds * 1000)), category)) {
                    armorStandMap.get(i).setCustomName(Main.color(format.replace("{name}", Main.playerNameFromId(UUID.fromString(lbe.getId()))).replace("{value}", "" + stat.format(lbe.getValue()))));
                    i++;
                }
            }

        }, 20L, refreshSeconds * 20L);
        Main.log("Initialized Leaderboard: " + getIdentifier());
    }

    /*
    Kills armorstands and cancels task when plugin disables (server stop/reload)
     */
    public void terminate() {
        task.cancel();
        for (ArmorStand as : armorStandMap.values()){
            as.remove();
        }
        armorStandMap.clear();
    }

    /*
    Saves leaderboard info to file. Run when leaderboard options are changed.
     */
    public void saveFile() {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        fileConfig.set("stat", stat.getIdentifier());
        fileConfig.set("title", title);
        fileConfig.set("category", category);
        fileConfig.set("size", size);
        fileConfig.set("recency-seconds", recentSeconds);
        fileConfig.set("refresh-seconds", refreshSeconds);
        fileConfig.set("location.world", location.getWorld().getName());
        fileConfig.set("location.x", location.getX());
        fileConfig.set("location.y", location.getY());
        fileConfig.set("location.z", location.getZ());
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        saveFile();
        terminate();
        initialize();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        saveFile();
        terminate();
        initialize();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        saveFile();
        terminate();
        initialize();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        saveFile();
        terminate();
        initialize();
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
        saveFile();
        terminate();
        initialize();
    }

}
