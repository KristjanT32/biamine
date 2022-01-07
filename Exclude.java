package com.krisapps.biamine.biamine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Exclude implements CommandExecutor {

    BiaMine main;

    public Exclude(BiaMine main) {
        this.main = main;
    }

    void addExclusion(String player, String game, CommandSender sender) throws IOException {
        if (main.games.contains(game)) {
            if (main.games.contains(game + ".exclude")) {
                List<String> exclude = (List<String>) main.games.getList(game + ".exclude");
                if (!exclude.contains(player)) {
                    exclude.add(player);
                    main.games.set(game + ".exclude", exclude);
                    main.games.save(main.gameConfigfile);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.msg-excl_added").replace("$p", player))));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-alreadyexcluded"))));
                }
            } else {
                List<String> exclude = new ArrayList<>();
                if (!exclude.contains(player)) {
                    exclude.add(player);
                    main.games.set(game + ".exclude", exclude);
                    main.games.save(main.gameConfigfile);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.msg-excl_added").replace("$p", player))));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-alreadyexcluded"))));
                }
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".start.err-notfound"))));
        }
    }

    void removeExclusion(String player, String game, CommandSender sender) throws IOException {
        if (main.games.contains(game + ".exclude")) {
            List<String> exclude = (List<String>) main.games.getList(game + ".exclude");
            if (exclude.contains(player)) {
                exclude.remove(player);
                main.games.set(game + ".exclude", exclude);
                main.games.save(main.gameConfigfile);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.msg-p_removed").replace("$p", player))));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-pnotfound"))));
            }
        } else {
            List<String> exclude = new ArrayList<>();
            if (exclude.contains(player)) {
                exclude.remove(player);
                main.games.set(game + ".exclude", exclude);
                main.games.save(main.gameConfigfile);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.msg-p_removed").replace("$p", player))));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-pnotfound"))));
            }
        }
    }

    void clearExclusionList(String game, CommandSender sender) throws IOException {
        if (main.games.contains(game)) {
            if (main.games.contains(game + ".exclude") && main.games.get(game + ".exclude") != null) {
                main.games.set(game + ".exclude", null);
                main.games.save(main.gameConfigfile);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.msg-listcleared").replace("$game", game))));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-noexlist"))));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".start.err-notfound"))));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /exclude <gameID> <player> [param]
        //Excludes player from the game.
        //Add -r to remove player from exclusion.

        //ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".value"))

        if (sender instanceof Player) {
            if (args.length == 3) {
                //Case where /exclude <gameID> <player> [-r]
                if (main.games.contains(args[0])) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        switch (args[1]) {
                            case "clear":
                                try {
                                    clearExclusionList(args[0], sender);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "view":
                                sender.sendMessage(ChatColor.YELLOW + "========================================");
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.phr-listexcl"))));
                                for (Object el : Objects.requireNonNull(main.games.getList(args[0] + ".exclude"))) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.phr-excluding"))) + ChatColor.AQUA + el.toString());
                                }
                                sender.sendMessage(ChatColor.YELLOW + "========================================");
                                break;
                            default:
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-invalidop"))));
                        }
                    } else {
                        if (args[2] != null && args[2].equals("-r")) {
                            try {
                                removeExclusion(args[1], args[0], sender);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                addExclusion(args[1], args[0], sender);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".start.err-notfound"))));
                }
            } else if (args.length == 2) {
                //Case where either /exclude <gameID> list/view or /exclude <gameID> <player>
                if (main.games.contains(args[0])) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        switch (args[1]) {
                            case "clear":
                                try {
                                    clearExclusionList(args[0], sender);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "view":
                                sender.sendMessage(ChatColor.YELLOW + "========================================");
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.phr-listexcl"))));
                                if (main.games.contains(args[0] + ".exclude")) {
                                    for (Object el : Objects.requireNonNull(main.games.getList(args[0] + ".exclude"))) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.phr-excluding"))) + ChatColor.AQUA + el.toString());
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-noexlist"))));
                                }
                                sender.sendMessage(ChatColor.YELLOW + "========================================");
                                break;
                            default:
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-invalidop"))));
                        }
                    } else {
                        try {
                            addExclusion(args[1], args[0], sender);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-syntaxerr"))));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".exclude.err-onlyplayer"))));
        }
        return true;
    }
}
