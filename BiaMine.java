package com.krisapps.biamine.biamine;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class BiaMine extends JavaPlugin {

    File gameConfigfile = new File(this.getDataFolder(), "games.yml");
    File pluginConfigfile = new File(this.getDataFolder(), "config.yml");
    File locfile = new File(getDataFolder(), "localization.yml");

    FileConfiguration games;
    FileConfiguration config;
    FileConfiguration localization;

    File logFile = new File(this.getDataFolder(), "biamine.log");

    void register() {

        //Register plugin commands

        getLogger().info(ChatColor.YELLOW + "Registering components...");
        getCommand("creategame").setExecutor(new CreateGame(this));
        getCommand("setstart").setExecutor(new SetStart(this));
        getCommand("startgame").setExecutor(new StartGame(this));
        getCommand("stopgame").setExecutor(new StopGame(this));
        getCommand("selectgame").setExecutor(new SelectGame(this));
        getCommand("reloadgame").setExecutor(new ReloadGame(this));
        getCommand("finishgame").setExecutor(new FinishGame(this));
        getCommand("getinfo").setExecutor(new GetInfo(this));
        getCommand("count").setExecutor(new Count(this));
        getCommand("editgame").setExecutor(new EditGame(this));
        getCommand("deletegame").setExecutor(new DeleteGame(this));
        getCommand("globalvarinfo").setExecutor(new GlobalVarInfo(this));
        getCommand("lang").setExecutor(new Language(this));
        getCommand("verifyfiles").setExecutor(new FileIntegrity(this));
        getCommand("exclude").setExecutor(new Exclude(this));


        //Register tab completers.
        getCommand("deletegame").setTabCompleter(new DelGameAC(this));
        getCommand("editgame").setTabCompleter(new EditAC(this));
        getCommand("exclude").setTabCompleter(new ExcludeAC(this));
        getCommand("getinfo").setTabCompleter(new GetInfoAC(this));
        getCommand("lang").setTabCompleter(new LanAC());
        getCommand("reloadgame").setTabCompleter(new GameOperationsAC(this));
        getCommand("selectgame").setTabCompleter(new GameOperationsAC(this));
        getCommand("stopgame").setTabCompleter(new GameOperationsAC(this));
        getCommand("startgame").setTabCompleter(new GameOperationsAC(this));
        getCommand("setstart").setTabCompleter(new GameOperationsAC(this));


        getLogger().info(ChatColor.GREEN + "Successfully registered required components!");
        final List<String> supportedLanguages = Arrays.asList("enUS", "ruRU");
        if (!supportedLanguages.contains(config.getString("lang"))) {
            getLogger().info(ChatColor.RED + "Warning! Language code saved in the config file is not valid.");
        }

    }

    void configure() {

        //Configure plugin files

        getLogger().info(ChatColor.YELLOW + "Configuring files...");

        if (!gameConfigfile.getParentFile().exists() || !gameConfigfile.exists()) {
            getLogger().info(ChatColor.RED + "Biathlon Game Config not found. Creating a new file...");
            gameConfigfile.getParentFile().mkdirs();
            saveResource("games.yml", false);
        } else {
            getLogger().info(ChatColor.GREEN + "Existing game config detected. Skipping...");
        }
        if (!pluginConfigfile.getParentFile().exists() || !pluginConfigfile.exists()) {
            getLogger().info(ChatColor.RED + "BiaMine Plugin Config not found. Creating a new file...");
            pluginConfigfile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        } else {
            getLogger().info(ChatColor.GREEN + "Existing config detected. Skipping...");
        }
        ////
        if (!locfile.getParentFile().exists() || !locfile.exists()) {
            getLogger().info(ChatColor.RED + "BiaMine Language file not found. Creating a new file...");
            locfile.getParentFile().mkdirs();
            saveResource("localization.yml", true);
        } else {
            getLogger().info(ChatColor.GREEN + "Existing localization file detected. Skipping...");
        }

        games = new YamlConfiguration();
        config = new YamlConfiguration();
        localization = new YamlConfiguration();
        try {
            games.load(gameConfigfile);
            config.load(pluginConfigfile);
            localization.load(locfile);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().info(ChatColor.RED + "Failed to load config files.");
            e.printStackTrace();
        }
        getLogger().info(ChatColor.GREEN + "Completed configuring plugin files.");
        getLogger().info("Starting plugin with language: " + config.getString("lang"));

    }

    void log(String message) throws IOException {

        //Write to log file

        if (config.getBoolean("debugging.log-changes")) {
            if (!logFile.getParentFile().exists()) {
                logFile.createNewFile();
            } else if (!logFile.exists()) {
                logFile.createNewFile();
            }
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
            FileWriter fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(String.format("[%s]: " + message, format.format(now)));
            pw.flush();
            pw.close();
        }
    }

    @Override
    public void onEnable() {
        configure();
        register();

    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "Disabling plugin...");
    }
}
