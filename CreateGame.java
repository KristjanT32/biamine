package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class CreateGame implements CommandExecutor {

    BiaMine main;

    public CreateGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /creategame <id> <countdown> <training_time> <display_name>
        if (args.length > 0) {
            if (args.length >= 4) {
                String display_name = "";
                for (int i = 3; i != args.length; i++) {
                    display_name += args[i] + " ";
                }
                main.games.set(args[0] + ".id", args[0]);
                main.games.set(args[0] + ".display_name", display_name);
                main.games.set(args[0] + ".countdown", args[1]);
                main.games.set(args[0] + ".training_time", args[2]);
                main.games.set("activeGame", args[0]);
                try {
                    main.games.save(main.gameConfigfile);
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-save")));
                    try {
                        main.log("Failed to save 'games.yml': File not found.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.msg-gameready").replace("$id", args[0])));
                try {
                    main.log("Game created: " + args[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.insuff")));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
