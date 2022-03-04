package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class ReloadGame implements CommandExecutor {

    BiaMine main;

    public ReloadGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /reloadgame <id>
        if (args.length > 0) {
            if (!args[0].equalsIgnoreCase("-active") && main.games.contains(args[0])) {
                String display_name = main.games.getString(args[0] + ".display_name");
                float trtime = Float.parseFloat(main.games.getString(args[0] + ".training_time"));
                float countdown = Float.parseFloat(main.games.getString(args[0] + ".countdown"));
                int shcount = main.games.getInt(args[0] + ".shootings");

                BiathlonGame game = new BiathlonGame(args[0], display_name, trtime, countdown, main, shcount);
                try {
                    game.reloadGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("-active")) {
                String display_name = main.games.getString(main.games.getString("activeGame") + ".display_name");
                float trtime = Float.parseFloat(main.games.getString(main.games.getString("activeGame") + ".training_time"));
                float countdown = Float.parseFloat(main.games.getString(main.games.getString("activeGame") + ".countdown"));
                int shcount = main.games.getInt(main.games.getString("activeGame") + ".shootings");

                BiathlonGame game = new BiathlonGame(main.games.getString("activeGame"), display_name, trtime, countdown, main, shcount);
                try {
                    game.reloadGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }
        return true;
    }
}
