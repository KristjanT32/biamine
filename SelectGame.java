package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Objects;

public class SelectGame implements CommandExecutor {

    BiaMine main;

    public SelectGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /selectgame <id>
        if (args.length > 0) {
            if (main.games.contains(args[0])) {
                if (!Objects.equals(main.games.getString("activeGame"), args[0])) {
                    main.games.set("activeGame", args[0]);
                    try {
                        main.log("Game selected: " + args[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        main.games.save(main.gameConfigfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".select.msg-gameselected").replace("$g", args[0])));
                } else if (Objects.equals(main.games.getString("activeGame"), args[0])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".select.msg-nochng")));
                    try {
                        main.log("Failed to select game: Game already selected.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".select.err-notfound").replace("$g", args[0])));
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
