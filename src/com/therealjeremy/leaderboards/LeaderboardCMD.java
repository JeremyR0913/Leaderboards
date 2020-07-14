package com.therealjeremy.leaderboards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardCMD implements CommandExecutor {

    private Main plugin;
    private Map<String, String> permissionMap = new HashMap<>();
    private String stats = "&8(&cStats&8)";

    public LeaderboardCMD(Main plugin) {
        this.plugin = plugin;
        permissionMap.put("create", "jcore.admin.leaderboard.create");
        permissionMap.put("delete", "jcore.admin.leaderboard.delete");
        permissionMap.put("teleport", "jcore.admin.leaderboard.teleport");
        permissionMap.put("title", "jcore.admin.leaderboard.title");
        permissionMap.put("size", "jcore.admin.leaderboard.size");
        permissionMap.put("near", "jcore.admin.leaderboard.near");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            // TODO help command
            Map<String, String> helpMap = new HashMap<>();
            helpMap.put("create", "&cUsage: /leaderboard create [id] [stat]");
            helpMap.put("delete", "&aThis command is a work in progress.");
            helpMap.put("teleport", "&cUsage: /leaderboard teleport [id]");
            helpMap.put("title", "&cUsage: /leaderboard title [id] [title]");
            helpMap.put("size", "&cUsage: /leaderboard size [id] [size]");
            helpMap.put("near", "&cUsage: /leaderboard near (distance)");
            for (String command : helpMap.keySet()){
                String help = helpMap.get(command);
                sender.sendMessage(Main.color("&a" + command + "&7: " + help));
            }
            return true;
        }
        String command = args[0].toLowerCase();
        String permission = permissionMap.get(command);
        if (permission == null) {
            sender.sendMessage(Main.color(stats + "&cUnknown command."));
            return false;
        }
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Main.color(stats + "&cYou do not have permission to use this command."));
            Main.log("User " + sender.getName() + " was denied access to a command. Missing permission: '" + permission + "'.");
            return true;
        }
        boolean isConsole = !(sender instanceof Player);
        if (command.equals("create")) {
            if (isConsole) {
                sender.sendMessage(Main.color(stats + "&cYou must be a player to use this command."));
                return true;
            }
            Player player = (Player) sender;
            if (args.length < 3) {
                sender.sendMessage(Main.color(stats + "&cUsage: /leaderboard create [id] [stat]"));
                return true;
            }
            String leaderboardId = args[1];
            if (plugin.getStatManager().getLeaderboard(leaderboardId) != null){
                sender.sendMessage(Main.color(stats + "&cA leaderboard with this id already exists."));
                return true;
            }
            String statId = args[2];
            Stat stat = plugin.getStatManager().getStat(statId);
            if (stat == null){
                sender.sendMessage(Main.color(stats + "&cThat stat does not currently exist."));
                return true;
            }
            plugin.getStatManager().createLeaderboard(leaderboardId, player.getLocation(), stat);
            sender.sendMessage(Main.color(stats + "&7Leaderboard &e" + leaderboardId.toLowerCase() + " &7has been created."));
            return true;
        } else if (command.equals("delete")) {
            // TODO
            sender.sendMessage(Main.color(stats + "&aThis command is a work in progress."));
            return true;
        } else if (command.equals("teleport")) {
            if (isConsole) {
                sender.sendMessage(Main.color(stats + "&cYou must be a player to use this command."));
                return true;
            }
            Player player = (Player) sender;
            if (args.length < 2) {
                sender.sendMessage(Main.color(stats + "&cUsage: /leaderboard teleport [id]"));
                return true;
            }
            String leaderboardId = args[1];
            Leaderboard leaderboard = plugin.getStatManager().getLeaderboard(leaderboardId);
            if (leaderboard == null){
                sender.sendMessage(Main.color(stats + "&cA leaderboard with this id doesn't exists."));
                return true;
            }
            leaderboard.setLocation(player.getLocation());
            sender.sendMessage(Main.color(stats + "&7Leaderboard &e" + leaderboard.getIdentifier() + "&7's location has been set."));
            return true;
        } else if (command.equals("title")) {
            if (args.length < 3) {
                sender.sendMessage(Main.color(stats + "&cUsage: /leaderboard title [id] [title]"));
                return true;
            }
            String leaderboardId = args[1];
            Leaderboard leaderboard = plugin.getStatManager().getLeaderboard(leaderboardId);
            if (leaderboard == null){
                sender.sendMessage(Main.color(stats + "&cA leaderboard with this id doesn't exists."));
                return true;
            }
            String title = "";
            for (int i = 2; i < args.length; i++){
                title += args[i] + " ";
            }
            title = title.substring(0, title.length() - 1);
            leaderboard.setTitle(title);
            sender.sendMessage(Main.color(stats + "&7Leaderboard &e" + leaderboard.getIdentifier() + "&7's title has been set."));
            return true;
        } else if (command.equals("size")) {
            if (args.length < 3) {
                sender.sendMessage(Main.color(stats + "&cUsage: /leaderboard size [id] [size]"));
                return true;
            }
            String leaderboardId = args[1];
            Leaderboard leaderboard = plugin.getStatManager().getLeaderboard(leaderboardId);
            if (leaderboard == null){
                sender.sendMessage(Main.color(stats + "&cA leaderboard with this id doesn't exists."));
                return true;
            }
            if (!Main.isInt(args[2])){
                sender.sendMessage(Main.color(stats + "&c'" + args[2] + "' must be an integer."));
                return true;
            }
            leaderboard.setSize(Integer.parseInt(args[2]));
            sender.sendMessage(Main.color(stats + "&7Leaderboard &e" + leaderboard.getIdentifier() + "&7's size has been set."));
            return true;
        } else if (command.equals("near")) {
            if (isConsole) {
                sender.sendMessage(Main.color(stats + "&cYou must be a player to use this command."));
                return true;
            }
            Player player = (Player) sender;
            int radius = 10;
            if (args.length > 1 && Main.isInt(args[1])){
                radius = Integer.parseInt(args[1]);
            }
            sender.sendMessage(Main.color("&7=============================="));
            sender.sendMessage(Main.color("&7Nearby leaderboards: (radius = " + radius + ")"));
            boolean b = false;
            for (Leaderboard leaderboard : plugin.getStatManager().leaderboards()){
                double distance = leaderboard.getLocation().distance(player.getLocation());
                if (distance <= radius){
                    b = true;
                    sender.sendMessage(Main.color("&e" + leaderboard.getIdentifier() + "&7:"));
                    sender.sendMessage(Main.color("&7- Title: &r" + leaderboard.getTitle()));
                    sender.sendMessage(Main.color("&7- Distance: &e" + Main.decimalFormat.format(distance) + " blocks"));
                }
            }
            if (!b){
                sender.sendMessage(Main.color("&cNo leaderboards could be found in that radius."));
            }
            sender.sendMessage(Main.color("&7=============================="));
            return true;
        }
        return false;
    }

}
