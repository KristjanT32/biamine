package com.krisapps.biamine.biamine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class Count implements CommandExecutor {

    BiaMine main;

    public Count(BiaMine main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Announces the stopwatch time and subtracts from playersLeft
        if (main.games.getInt(main.games.getString("gameInProgress") + ".playersLeft") > 0) {
            for (String entry : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getEntries()) {
                if (entry.equals(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.players-left"))) || entry.contains(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.players-left")))) {
                    Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores(entry);
                }
            }
            main.games.set(main.games.getString("gameInProgress") + ".playersLeft", main.games.getInt(main.games.getString("gameInProgress") + ".playersLeft") - 1);
            main.games.set("latestFinishTime", main.games.getString("formattedDuration"));
            try {
                main.games.save(main.gameConfigfile);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".count.err-save")));
                try {
                    main.log("Failed to save file 'games.yml'");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
            try {
                main.log(String.format("Game %s: Player has finished their run: %s", main.games.getString("gameInProgress"), main.games.getString("formattedDuration")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.players-left") + ChatColor.YELLOW + main.games.getString(main.games.getString("gameInProgress") + ".playersLeft"))).setScore(-2);
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".count.msg-playerfinished").replace("$time", main.games.getString("formattedDuration"))));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".count.err-gamefinished")));
        }
        return false;
    }
}
