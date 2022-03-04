package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SetStart implements CommandExecutor {

    BiaMine main;

    public SetStart(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /setstart <id>
        if (args.length > 0) {
            Player player = (Player) sender;
            Location startLocation = player.getLocation();
            main.games.set(args[0] + ".start.x", startLocation.getX());
            main.games.set(args[0] + ".start.y", startLocation.getY());
            main.games.set(args[0] + ".start.z", startLocation.getZ());

            try {
                main.games.save(main.gameConfigfile);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".setstart.err-save")));
                e.printStackTrace();
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".setstart.msg-startset").replace("$g", args[0])));
            try {
                main.log("Start position for game " + args[0] + " set successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".creategame.err-insuff")));
        }

        return true;
    }
}
