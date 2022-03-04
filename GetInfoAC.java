package com.krisapps.biamine.biamine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetInfoAC implements TabCompleter {

    BiaMine main;

    public GetInfoAC(BiaMine main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Syntax: /getinfo <list/find> <id>
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("list");
            completions.add("find");
        } else if (args.length == 2) {
            List<String> forbiddenKeys = Arrays.asList("activeGame", "latestGameDuration", "gameInProgress", "formattedDuration", "latestFinishTime");
            for (String game : main.games.getKeys(false)) {
                if (!forbiddenKeys.contains(game)) {
                    completions.add(game);
                }
            }
        }

        return completions;
    }
}
