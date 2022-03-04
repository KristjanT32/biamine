package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class StopGame implements CommandExecutor {

    BiaMine main;

    public StopGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /stopgame <id>
        if (args.length > 0) {
            try {
                BiathlonGame game = new BiathlonGame(args[0], main.games.getString(args[0] + ".display_name"), Float.parseFloat(main.games.getString(args[0] + ".training_time")), Float.parseFloat(main.games.getString(args[0] + ".countdown")), main, Integer.parseInt(main.games.getString(main.games.getString("activeGame") + ".shootings")));
                try {
                    if (main.games.contains(main.games.getString("gameInProgress")) && !main.games.getString("gameInProgress").equalsIgnoreCase(null)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".stop.msg-stopping").replace("$conf", args[0])));
                        game.forceStopGame();
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".stop.err-notactive")));
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".biagame.err-noactive")));
                }
            } catch (NullPointerException | IOException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".stop.err-npe")));
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
