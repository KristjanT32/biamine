package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class EditGame implements CommandExecutor {

    BiaMine main;

    public EditGame(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /editgame <id> <property> <newValue>
        if (args.length >= 2) {
            if (main.games.contains(args[0])) {
                if (main.games.contains(args[0] + "." + args[1])) {
                    if (args[2] == null || args.length < 3) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".editgame.err-nullval")));
                        try {
                            main.log("Edit value: Failed to edit property. No value provided.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String oldVal = main.games.getString(args[0] + "." + args[1]);
                        main.games.set(args[0] + "." + args[1], args[2]);
                        try {
                            main.games.save(main.gameConfigfile);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".editgame.err-save")));
                            try {
                                main.log("Failed to save 'games.yml'.");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                        String newVal = main.games.getString(args[0] + "." + args[1]);
                        String message = ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".editgame.msg-valchnge").replace("$val", args[1]));
                        message = message.replace("$old", oldVal);
                        message = message.replace("$new", newVal);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        try {
                            main.log("Value of property [" + args[1] + "] was changed from [" + oldVal + "] to [" + newVal + "]");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".editgame.err-valnotfound")));
                }
            }
        }
        return true;
    }
}
