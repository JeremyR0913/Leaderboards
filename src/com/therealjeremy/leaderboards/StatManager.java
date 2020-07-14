package com.therealjeremy.leaderboards;

import com.therealjeremy.leaderboards.stats.StatBlocksMined;
import com.therealjeremy.leaderboards.stats.StatMobsKilled;
import com.therealjeremy.leaderboards.stats.StatTimePlayed;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatManager {

    // TODO
    // create method for leaderboard

    private Main plugin;

    public StatManager(Main plugin) {
        this.plugin = plugin;
        initializeStats();
        initializeLeaderboards();
    }

    private Map<String, Leaderboard> leaderboardMap = new HashMap<>();

    /*
    Loads leaderboards from file.
     */
    private void initializeLeaderboards(){
        File folder = new File(plugin.getDataFolder() + File.separator + "leaderboards");
        if (!folder.exists()){
            Main.log("Generating 'leaderboards' folder...");
            folder.mkdirs();
        }
        for (File file : folder.listFiles()){
            if (file.getName().endsWith(".yml")){
                FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
                String statIdentifier = fileConfig.getString("stat");
                if (statIdentifier == null || getStat(statIdentifier) == null){
                    Main.log("Could not register leaderboard '" + file.getName() + "'. Unknown stat: " + statIdentifier);
                    continue;
                }
                Leaderboard leaderboard = new Leaderboard(plugin, file, getStat(statIdentifier));
                leaderboard.initialize();
                leaderboardMap.put(leaderboard.getIdentifier().toLowerCase(), leaderboard);
            }
        }
    }

    private Map<String, Stat> statMap = new HashMap<>();

    private void initializeStats(){
        initializeStat(new StatMobsKilled(plugin));
        initializeStat(new StatBlocksMined(plugin));
        initializeStat(new StatTimePlayed(plugin));
    }

    private void initializeStat(Stat stat){
        try {
            stat.initialize(plugin.getSqlConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statMap.put(stat.getIdentifier().toLowerCase(), stat);
    }

    public void terminate(){
        for (Stat stat : statMap.values()){
            stat.terminate();
        }
        for (Leaderboard leaderboard : leaderboardMap.values()){
            leaderboard.terminate();
        }
    }

    public void createLeaderboard(String identifier, Location location, Stat stat){
        Leaderboard leaderboard = new Leaderboard(plugin, identifier, location, stat);
        leaderboard.initialize();
        leaderboardMap.put(leaderboard.getIdentifier().toLowerCase(), leaderboard);
    }

    public Map<String, Stat> getStatMap() {
        return statMap;
    }

    public Stat getStat(String identifier){
        return getStatMap().get(identifier.toLowerCase());
    }

    public Collection<Stat> stats(){
        return getStatMap().values();
    }

    public Map<String, Leaderboard> getLeaderboardMap() {
        return leaderboardMap;
    }

    public Leaderboard getLeaderboard(String identifier){
        return getLeaderboardMap().get(identifier.toLowerCase());
    }

    public Collection<Leaderboard> leaderboards(){
        return getLeaderboardMap().values();
    }

}
