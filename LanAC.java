package com.krisapps.biamine.biamine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LanAC implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Available languages: enUS, ruRU
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("enUS");
            completions.add("ruRU");
        } else if (args.length > 1) {
            completions.clear();
        }
        return completions;
    }
}
