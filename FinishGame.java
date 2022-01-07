package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class FinishGame implements CommandExecutor {

    BiaMine main;

    public FinishGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /finishgame
        String id = main.games.getString("gameInProgress");
        String display_name = main.games.getString(id + ".display_name");
        float trtime = Float.parseFloat(main.games.getString(id + ".training_time"));
        float countdown = Float.parseFloat(main.games.getString(id + ".countdown"));
        int shcount = main.games.getInt(id + ".shootings");
        BiathlonGame game = new BiathlonGame(id, display_name, trtime, countdown, main, shcount);
        if (id != null) {
            try {
                game.finishGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
            main.games.set("gameInProgress", null);
            try {
                main.games.save(main.gameConfigfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".finishgame.err-noactive")));
        }
        return true;
    }
}
