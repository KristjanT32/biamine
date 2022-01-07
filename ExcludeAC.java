package com.krisapps.biamine.biamine;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcludeAC implements TabCompleter {

    BiaMine main;
    public ExcludeAC(BiaMine main){
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Syntax: /exclude <gameID> <player> [param]
        List<String> completions = new ArrayList<>();
        if (args.length == 1){
            List<String> forbiddenKeys = Arrays.asList("activeGame", "latestGameDuration", "gameInProgress", "formattedDuration", "latestFinishTime");
            for (String game : main.games.getKeys(false)) {
                if (!forbiddenKeys.contains(game)){
                    completions.add(game);
                }
            }
        }else if (args.length == 2){
            completions.add("view");
            completions.add("clear");
            for (Player p: Bukkit.getOnlinePlayers()){
                completions.add(p.getName());
            }
        }else if (args.length == 3){
            completions.add("-r");
        }

        return completions;
    }
}
