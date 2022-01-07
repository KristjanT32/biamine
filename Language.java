package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Language implements CommandExecutor {

    BiaMine main;

    public Language(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /lang <langCode>

        final List<String> supportedLanguages = Arrays.asList("enUS", "ruRU");
        if (supportedLanguages.contains(args[0])) {
            main.config.set("lang", args[0]);
            try {
                main.config.save(main.pluginConfigfile);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".lang.err-save")));
                e.printStackTrace();
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".lang.msg-lanchnge").replace("$lang", args[0])));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".lang.err-notfound").replace("$lang", args[0])));
            return false;
        }
        return true;
    }
}
