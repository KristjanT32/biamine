package com.krisapps.biamine.biamine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditAC implements TabCompleter {

    BiaMine main;

    public EditAC(BiaMine main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Syntax: /editgame <id> <property> <newValue>
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> forbiddenKeys = Arrays.asList("activeGame", "latestGameDuration", "gameInProgress", "formattedDuration", "latestFinishTime");
            for (String game : main.games.getKeys(false)) {
                if (!forbiddenKeys.contains(game)) {
                    completions.add(game);
                }
            }
        } else if (args.length == 2) {
            completions.add("display_name");
            completions.add("countdown");
            completions.add("training_time");
            completions.add("start.x");
            completions.add("start.y");
            completions.add("start.z");
        }
        return completions;
    }
}
