package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StartGame implements CommandExecutor {

    BiaMine main;

    public StartGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /startgame <id> <shootings> <mode>
        List<String> legalGameModes = Arrays.asList("MASS-START", "INDIVIDUAL", "RELAY", "SPRINT");

        if (args.length == 3) {
            if (main.games.contains(args[0])) {
                if (legalGameModes.contains(args[2])) {
                    BiathlonGame game = new BiathlonGame(args[0], main.games.getString(args[0] + ".display_name"), Float.parseFloat(main.games.getString(args[0] + ".training_time")), Float.parseFloat(main.games.getString(args[0] + ".countdown")), main, Integer.parseInt(args[1]));
                    String id = args[0];
                    try {
                        game.startGame();
                    } catch (Exception e) {
                        sender.sendMessage(e.getMessage());
                        e.printStackTrace();
                    }
                    main.games.set(args[0] + ".shootings", args[1]);
                    main.games.set(args[0] + ".mode", args[2]);
                    try {
                        main.games.save(main.gameConfigfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (main.config.getBoolean("configuration.alert-configuration-started")) {
                        String inf = ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".start.msg-confstarted").replace("$time", main.games.getString(id + ".training_time")));
                        inf = inf.replace("$conf", args[0]);
                        sender.sendMessage(inf);
                    }
                }else{
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".start.err-illegalmode")));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".start.err-notfound")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".start.err-nullval")));
        }
        return true;
    }
}
