package com.therealjeremy.leaderboards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLCMD implements CommandExecutor {

    private List<Player> playersEnabled = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("leaderboard.sql")){
            sender.sendMessage(Main.color("&8(&cStats&8) " + "&cYou do not have permission to use this command."));
            return true;
        }
        if (args.length == 0){
            sender.sendMessage(Main.color("&8(&cStats&8) " + "&cUsage: /jsql [query] *make sure you know what you are doing!*"));
            return true;
        }
        if (!(sender instanceof ConsoleCommandSender)){
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("toggle")){
                if (playersEnabled.contains(player)){
                    playersEnabled.remove(player);
                    sender.sendMessage(Main.color("&8(&cStats&8) " + "&7SQL feature toggled off."));
                }else{
                    playersEnabled.add(player);
                    sender.sendMessage(Main.color("&8(&cStats&8) " + "&7SQL feature toggled on. *make sure you know what you are doing!*"));
                }
                return true;
            }
            if (!playersEnabled.contains(player)){
                sender.sendMessage(Main.color("&8(&cStats&8) " + "&cUse '/jsql toggle' to enable this feature."));
                return true;
            }
        }
        String query = "";
        for (String s : args){
            query += s + " ";
        }
        query = query.substring(0, query.length() - 1);
        Statement statement = null;
        try {
            statement = Main.getSqlConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            query = null;
            while (set.next()){
                String s = "";
                for (int i = 1; i <= set.getMetaData().getColumnCount(); i++){
                    String value = set.getString(i);
                    String columnName = set.getMetaData().getColumnName(i);
                    s += "(" + columnName +") " + value + ", ";
                }
                sender.sendMessage(s.substring(0, s.length() - 1));
            }
        } catch (SQLException e) {
            if (!e.getMessage().equals("query does not return ResultSet")){
                e.printStackTrace();
            }
        }finally{
            try {
                assert statement != null;
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        sender.sendMessage(Main.color("&8(&cStats&8) " + "&7Query executed."));
        return true;
    }

}
