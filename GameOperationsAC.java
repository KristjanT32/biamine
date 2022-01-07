package com.krisapps.biamine.biamine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameOperationsAC implements TabCompleter {

    BiaMine main;

    public GameOperationsAC(BiaMine main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> forbiddenKeys = Arrays.asList("activeGame", "latestGameDuration", "gameInProgress", "formattedDuration", "latestFinishTime");
        List<String> completions = new ArrayList<>();
        switch (command.getName()) {
            case "reloadgame":
            case "selectgame":
            case "setstart":
            case "stopgame":
                if (args.length == 1) {
                    for (String game : main.games.getKeys(false)) {
                        if (!forbiddenKeys.contains(game)) {
                            completions.add(game);
                        }
                    }
                }
                break;
            case "startgame":
                if (args.length == 1) {
                    for (String game : main.games.getKeys(false)) {
                        if (!forbiddenKeys.contains(game)) {
                            completions.add(game);
                        }
                    }
                } else if (args.length == 3) {
                    completions.add("MASS-START");
                    completions.add("INDIVIDUAL");
                    completions.add("SPRINT");
                    completions.add("RELAY");
                }
                break;

        }

        return completions;
    }
}
