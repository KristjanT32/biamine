package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetInfo implements CommandExecutor {

    List<String> forbiddenKeys = Arrays.asList("activeGame", "latestGameDuration", "gameInProgress", "formattedDuration", "latestFinishTime");

    BiaMine main;

    public GetInfo(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /getinfo <list/find> <id>
        if (args.length > 0) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("find")) {
                    if (main.games.contains(args[1])) {
                        String id = args[1];
                        String infoString = ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.msg-infomsg").replace("$dname", ChatColor.translateAlternateColorCodes('&', main.games.getString(id + ".display_name"))));
                        infoString = infoString.replace("$trt", main.games.getString(id + ".training_time"));
                        infoString = infoString.replace("$ctd", main.games.getString(id + ".countdown"));
                        sender.sendMessage(ChatColor.GREEN + "========================================\n" + ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.phr-infoabout")) + ChatColor.AQUA + args[1] + "\n" + infoString + "\n========================================");
                        try {
                            main.log("Requested information about game: " + id);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ArrayList<String> possibleMatches = new ArrayList<>();
                        for (String game : main.games.getKeys(false)) {
                            if (!forbiddenKeys.contains(game)) {
                                if (game.contains(args[1])) {
                                    possibleMatches.add(game);
                                }
                            }
                        }
                        if (possibleMatches.size() > 0) {
                            sender.sendMessage(ChatColor.GREEN + "========================================\n" + ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.phr-possiblematches")) + "\n");
                            for (String match : possibleMatches) {
                                sender.sendMessage(ChatColor.AQUA + match + "\n");
                            }
                            sender.sendMessage(ChatColor.GREEN + "========================================");


                        } else {
                            sender.sendMessage(ChatColor.GREEN + "========================================\n");
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.err-notfound")));
                            sender.sendMessage(ChatColor.GREEN + "========================================\n");
                        }

                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(ChatColor.GREEN + "========================================\n" + ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.phr-list")) + "\n");
                    for (String tag : main.games.getKeys(false)) {
                        if (!forbiddenKeys.contains(tag)) {
                            sender.sendMessage(ChatColor.YELLOW + tag + "\n");
                        }
                    }
                    sender.sendMessage(ChatColor.GREEN + "========================================\n");
                    try {
                        main.log("Game list requested.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.err-notsupported").replace("$op", args[0])));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".getinfo.err-insuff")));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
