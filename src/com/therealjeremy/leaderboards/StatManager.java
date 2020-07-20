package com.therealjeremy.leaderboards;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private void initializeLeaderboards() {
        File folder = new File(plugin.getDataFolder() + File.separator + "leaderboards");
        if (!folder.exists()) {
            Main.log("Generating 'leaderboards' folder...");
            folder.mkdirs();
        }
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
                String statIdentifier = fileConfig.getString("stat");
                if (statIdentifier == null || getStat(statIdentifier) == null) {
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

    private void initializeStats() {
        File folder = new File(plugin.getDataFolder() + File.separator + "statistics");
        if (!folder.exists()) {
            Main.log("Generating 'statistics' folder...");
            folder.mkdirs();
        }
        JarLoader loader = new JarLoader(plugin);
        for (File file : folder.listFiles(pathname -> pathname.getName().endsWith(".jar"))) {

            Set<Class<?>> statClasses = null;
            try {
                statClasses = loader.myClassLoader(file);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            for (Class<?> statClass : statClasses) {
                if (statClass == null) {
                    continue;
                }
                Constructor constructor = null;
                try {
                    constructor = statClass.getConstructor(Main.class);
                } catch (NoSuchMethodException ignored) {
                }
                Stat stat = null;
                try {
                    if (constructor != null) {
                        stat = (Stat) constructor.newInstance(plugin);
                    } else {
                        stat = (Stat) statClass.newInstance();
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    continue;
                }
                initializeStat(stat);
            }

        }
        initializeStat(new StatTimePlayed(plugin));
    }

    private void initializeStat(Stat stat) {
        try {
            stat.initialize(plugin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statMap.put(stat.getIdentifier().toLowerCase(), stat);
    }

    public void terminate() {
        for (Stat stat : statMap.values()) {
            stat.terminate();
        }
        for (Leaderboard leaderboard : leaderboardMap.values()) {
            leaderboard.terminate();
        }
    }

    public void createLeaderboard(String identifier, Location location, Stat stat) {
        Leaderboard leaderboard = new Leaderboard(plugin, identifier, location, stat);
        leaderboard.initialize();
        leaderboardMap.put(leaderboard.getIdentifier().toLowerCase(), leaderboard);
    }

    public Map<String, Stat> getStatMap() {
        return statMap;
    }

    public Stat getStat(String identifier) {
        return getStatMap().get(identifier.toLowerCase());
    }

    public Collection<Stat> stats() {
        return getStatMap().values();
    }

    public Map<String, Leaderboard> getLeaderboardMap() {
        return leaderboardMap;
    }

    public Leaderboard getLeaderboard(String identifier) {
        return getLeaderboardMap().get(identifier.toLowerCase());
    }

    public Collection<Leaderboard> leaderboards() {
        return getLeaderboardMap().values();
    }

}
