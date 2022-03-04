package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class DeleteGame implements CommandExecutor {

    BiaMine main;

    public DeleteGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                if (main.games.contains(args[0])) {
                    main.games.set(args[0], null);
                    try {
                        main.games.save(main.gameConfigfile);
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".deletegame.err-save")));
                        try {
                            main.log("Failed to save 'games.yml'.");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".deletegame.msg-gamedeleted").replace("$id", args[0])));
                    try {
                        main.log("Game deleted: " + args[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".deletegame.err-notfound")));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".deletegame.err-insuff")));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
