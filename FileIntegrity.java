package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileIntegrity implements CommandExecutor {

    static public List<String> requiredFiles = Arrays.asList("games.yml", "localization.yml", "config.yml");
    static BiaMine main;

    public FileIntegrity(BiaMine main) {
        FileIntegrity.main = main;
    }

    void verify(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".vfi.status-searching")));
        if (main.getDataFolder().exists()) {
            List<File> files = Arrays.asList(main.getDataFolder().listFiles());

            for (String file : requiredFiles) {
                main.getLogger().info("Searching for: " + main.getDataFolder() + "/" + file + "\nStatus: " + files.contains(main.getDataFolder() + file));
                if (!new File(main.getDataFolder() + "/" + file).exists()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".vfi.status-filemissing").replace("$file", file)));
                    main.configure();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".vfi.status-filesgenerated")));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".vfi.status-nodatafolder")));
            main.configure();
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".vfi.status-allgood")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Syntax: /verifyfiles
        verify(sender);
        return true;
    }
}
