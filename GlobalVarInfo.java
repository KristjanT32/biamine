package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Objects;

public class GlobalVarInfo implements CommandExecutor {

    BiaMine main;

    public GlobalVarInfo(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String inf = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.localization.getString(main.config.getString("lang") + ".gvi.msg-info"))).replace("$act", main.games.getString("activeGame"));
        if (main.games.getString("gameInProgress") != null) {
            inf = inf.replace("$inp", Objects.requireNonNull(main.games.getString("gameInProgress")));
        } else {
            inf = inf.replace("$inp", "N/A");
        }
        inf = inf.replace("$frmt", Objects.requireNonNull(main.games.getString("formattedDuration")));
        inf = inf.replace("$dur", Objects.requireNonNull(main.games.getString("latestGameDuration")));
        try {
            main.log("Global variable info requested.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(inf);
        return true;
    }
}
