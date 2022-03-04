package com.krisapps.biamine.biamine;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BiathlonGame {

    private final int shootings;
    private final ArrayList<Player> players = new ArrayList<>();
    public Scoreboard game_board = Bukkit.getScoreboardManager().getMainScoreboard();
    BossBar bar_training;
    BossBar bar_countdown;
    Location start;
    Location finish;
    BiaMine main;
    File langfile;
    String formattedTime = "00:00";
    private String id = "";
    private String display_name = "";

    private float training_countdown = 0f;
    private float main_countdown = 0f;

    //TaskIDs
    private int trainingTask;
    private int countdownTask;
    private int tr_timertime;
    private int cd_timertime;

    //Timer variables
    private int seconds;
    private int minutes;
    private int hours;

    public BiathlonGame(String id, String display_name, float training_countdown, float main_countdown, BiaMine main, int shootings) {
        this.setDisplay_name(display_name);
        this.setId(id);
        this.setMain_countdown(main_countdown);
        this.setTraining_countdown(training_countdown);
        this.main = main;
        this.shootings = shootings;

        bar_countdown = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);

        for (Player p : Bukkit.getOnlinePlayers()) {
            bar_countdown.addPlayer(p);
        }
        bar_countdown.setTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.config.getString("bossbars.countdown_bossbar-standby"))));
        bar_countdown.setProgress(0.0);
        bar_countdown.setVisible(false);
    }

    public void reset() { //Reset all data to start a new game.
        bar_countdown.setVisible(false);
        game_board.clearSlot(DisplaySlot.SIDEBAR);
        for (String entry : game_board.getEntries()) { //Reset scoreboard entries.
            game_board.resetScores(entry);
        }
    }


    public String startGame() throws Exception { //Starts the game
        final String path = main.config.getString("lang") + ".biagame";
        reset();
        if (main.games.contains(id + ".start")) {
            constructScoreboard();
            startTraining(getTraining_countdown());
            start = new Location(Bukkit.getWorld("world"), main.games.getDouble(id + ".start.x"), main.games.getDouble(id + ".start.y"), main.games.getDouble(id + ".start.z"));

            if (main.config.getBoolean("configuration.alert-item_dispensing")) {
                Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".biagame.msg-dispensing").replace("$sh", String.valueOf(shootings))));
                Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
            }

            //Prepare scoreboard data; save it to file.
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!main.games.contains(id + ".exclude") || !main.games.getList(id + ".exclude").contains(p.getDisplayName())) {
                    p.teleport(start);
                    if (!p.getInventory().contains(new ItemStack(Material.BOW), 1) && !p.getInventory().contains(new ItemStack(Material.ARROW), shootings * 5)) {
                        if (main.config.getBoolean("configuration.report-items_dispensed")) {
                            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".biagame.msg-dispense-report").replace("$count", String.valueOf(shootings * 5))));
                            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
                            main.log("Game " + id + ": Dispensing items.");
                        }
                        p.getInventory().clear();
                        p.getInventory().addItem(new ItemStack(Material.BOW, 1));
                        p.getInventory().addItem(new ItemStack(Material.ARROW, shootings * 5));
                    } else {
                        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".biagame.msg-already-have-items")));
                        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
                    }
                    players.add(p);
                }
            }
            game_board.getObjective("time").setDisplayName(ChatColor.translateAlternateColorCodes('&', main.games.getString(id + ".display_name") + " " + main.localization.getString(main.config.getString("lang") + ".scoreboard.status-inprogress")));
            main.games.set(id + ".players", players.toArray().length);
            main.games.set(id + ".playersLeft", players.toArray().length);
            main.games.set("gameInProgress", id);

            try {
                main.games.save(main.gameConfigfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new Exception("No start position was found.");
        }
        main.log(String.format("Game %s: Game started.", id));
        return null;
    }

    public void reloadGame() throws IOException { //Stops and reloads the game info.
        final String path = main.config.getString("lang") + ".biagame";
        bar_countdown.setVisible(false);
        game_board.clearSlot(DisplaySlot.SIDEBAR);
        Bukkit.getScheduler().cancelTasks(main);
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(path + ".msg-reload").replace("$name", id)));
        start = new Location(Bukkit.getWorld("world"), main.games.getDouble(id + ".start.x"), main.games.getDouble(id + ".start.y"), main.games.getDouble(id + ".start.z"));
        bar_countdown.setTitle(ChatColor.translateAlternateColorCodes('&', main.config.getString("bossbars.countdown_bossbar")));
        for (String entry : game_board.getEntries()) {
            game_board.resetScores(entry);
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(path + ".msg-reload-complete")));
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
        main.log(String.format("Game %s: Game configuration reloaded.", id));
    }

    public void forceStopGame() throws IOException { //Force-stops the game
        final String path = main.config.getString("lang") + ".biagame";
        if (main.games.contains(main.games.getString("gameInProgress")) && !main.games.getString("gameInProgress").equalsIgnoreCase(null)) {
            bar_countdown.setVisible(false);
            reloadGame();
            Bukkit.getScheduler().cancelTasks(main);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(start);
            }
            main.games.set("gameInProgress", null);
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(path + ".msg-game-stopped").replace("$sec", main.games.getString("latestGameDuration"))));
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
            main.log(String.format("Game %s: Game terminated.", id));
        } else {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString("biagame.err-noactive")));
        }
    }

    public void finishGame() throws IOException { //Finishes the game
        final String path = main.config.getString("lang") + ".biagame";
        game_board.getObjective("time").setDisplayName(ChatColor.translateAlternateColorCodes('&', main.games.getString(id + ".display_name") + " " + main.localization.getString(main.config.getString("lang") + ".scoreboard.status-finished")));
        Bukkit.getScheduler().cancelTasks(main);
        bar_countdown.setVisible(false);
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
        String message = main.localization.getString(path + ".msg-gamefinished").replace("$dur", main.games.getString("formattedDuration"));
        message = message.replace("$sh", main.games.getString(id + ".shootings"));
        message = message.replace("$players", main.games.getString(id + ".players"));
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.replace("$instance", id)));
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "================================================");
        main.log(String.format("Game %s: Game finished.", id));
        game_board.getObjective("time").setDisplayName(ChatColor.translateAlternateColorCodes('&', main.games.getString(id + ".display_name") + " " + main.localization.getString(main.config.getString("lang") + ".scoreboard.status-finished")));
        if (main.config.getBoolean("configuration.clear-lft-after-finished")) {
            main.games.set("latestFinishTime", "N/A");
            main.games.save(main.gameConfigfile);
        }
        game_board.resetScores(ChatColor.AQUA + "Осталось: " + ChatColor.YELLOW + main.games.getString(id + ".playersLeft"));
    }

    void checkState() throws IOException {
        if (main.config.getBoolean("configuration.auto-finish-game")) {
            int pLeft = main.games.getInt(id + ".playersLeft");
            if (pLeft <= 0) {
                finishGame();
            }
        }
    }

    public void releasePlayers() throws IOException {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.removePotionEffect(PotionEffectType.SLOW);
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', main.config.getString("titles.gotitle")), "......", 1, 6, 2);
            bar_countdown.setVisible(false);
            startStopwatch();
        }
    }

    public void constructScoreboard() throws IOException { //Construct the components of a scoreboard.
        if (game_board.getObjective("time") == null) {
            game_board.registerNewObjective("time", "dummy", formattedTime, RenderType.INTEGER);
        }
        game_board.getObjective("time").setDisplaySlot(DisplaySlot.SIDEBAR);
        main.log(String.format("Game %s: Scoreboard setup complete.", id));
    }

    public void startTraining(float training_countdown) throws IOException {
        final String path = main.config.getString("lang") + ".biagame";
        bar_countdown.setTitle(ChatColor.translateAlternateColorCodes('&', main.config.getString("bossbars.countdown_bossbar-standby")));
        bar_countdown.setVisible(true);
        setTr_timertime((int) training_countdown);
        getMain().getLogger().info("Started training countdown");
        main.log(String.format("Game %s: Training started.", id));
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        setTrainingTask(scheduler.scheduleSyncRepeatingTask(getMain(), new Runnable() {
            @Override
            public void run() {
                if (getTr_timertime() > 0) {
                    setTr_timertime(getTr_timertime() - 1);
                    if (getTr_timertime() <= 5) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(path + ".msg-training-report").replace("$time", String.valueOf(getTr_timertime()))));
                    }
                } else if (getTr_timertime() <= 0) {
                    try {
                        finishTraining();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        startCountdown(getMain_countdown());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0L, 20L));
    }

    public void startCountdown(float main_countdown) throws IOException {
        final String path = main.config.getString("lang") + ".biagame";
        bar_countdown.setTitle(ChatColor.translateAlternateColorCodes('&', main.config.getString("bossbars.countdown_bossbar")));
        bar_countdown.setVisible(true);
        setCd_timertime((int) main_countdown);
        getMain().getLogger().info("Started countdown");
        main.log(String.format("Game %s: Countdown started.", id));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!main.games.contains(id + ".exclude") || !main.games.getList(id + ".exclude").contains(p.getDisplayName())) {
                PotionEffect e = new PotionEffect(PotionEffectType.SLOW, 999999, 2555);
                p.addPotionEffect(e, true);
            }
        }
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        setTrainingTask(scheduler.scheduleSyncRepeatingTask(getMain(), new Runnable() {
            @Override
            public void run() {
                if (getCd_timertime() > 0) {
                    setCd_timertime(getCd_timertime() - 1);
                    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', main.localization.getString(path + ".msg-startin").replace("$time", String.valueOf(getCd_timertime()))));
                    switch (getCd_timertime()) {
                        case 5:
                            bar_countdown.setProgress(.0);
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b5")).setScore(-5);
                            break;
                        case 4:
                            bar_countdown.setProgress(.2);
                            game_board.resetScores(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b5"));
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b4")).setScore(-5);
                            break;
                        case 3:
                            bar_countdown.setProgress(.4);
                            game_board.resetScores(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b4"));
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b3")).setScore(-5);
                            break;
                        case 2:
                            bar_countdown.setProgress(.6);
                            game_board.resetScores(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b3"));
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b2")).setScore(-5);
                            break;
                        case 1:
                            bar_countdown.setProgress(.8);
                            game_board.resetScores(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b2"));
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b1")).setScore(-5);
                            break;
                        case 0:
                            bar_countdown.setProgress(1);
                            game_board.resetScores(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.startingin") + "&b1"));
                            game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.gettingready"))).setScore(-5);

                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.setLevel(getCd_timertime());
                    }
                } else if (getCd_timertime() <= 1) {
                    Bukkit.getScheduler().cancelTask(getCountdownTask());
                    try {
                        finishCountdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0L, 20L));
    }

    public void finishCountdown() throws IOException {
        Bukkit.getScheduler().cancelTask(getCountdownTask());
        getMain().getLogger().info("Cancelled task: " + getCountdownTask() + " [Countdown]\nState: " + Bukkit.getScheduler().isCurrentlyRunning(getCountdownTask()));
        Bukkit.getScheduler().cancelTask(getTrainingTask());
        game_board.resetScores(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.gettingready"))));
        releasePlayers();
        main.log(String.format("Game %s: Countdown finished.", id));
    }

    public void finishTraining() throws IOException {
        Bukkit.getScheduler().cancelTask(getTrainingTask());
        getMain().getLogger().info("Cancelled task: " + getTrainingTask() + " [Training Countdown]");
        main.log(String.format("Game %s: Training finished.", id));
    }


    //Setters and getters.

    public float getTraining_countdown() {
        return training_countdown;
    }

    public void setTraining_countdown(float training_countdown) {
        this.training_countdown = training_countdown;
    }

    public int getTrainingTask() {
        return trainingTask;
    }

    public void setTrainingTask(int trainingTask) {
        this.trainingTask = trainingTask;
    }

    public int getCountdownTask() {
        return countdownTask;
    }

    public void setCountdownTask(int countdownTask) {
        this.countdownTask = countdownTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public float getMain_countdown() {
        return main_countdown;
    }

    public void setMain_countdown(float main_countdown) {
        this.main_countdown = main_countdown;
    }

    public BiaMine getMain() {
        return main;
    }

    public void setMain(BiaMine main) {
        this.main = main;
    }

    public int getTr_timertime() {
        return tr_timertime;
    }

    public void setTr_timertime(int tr_timertime) {
        this.tr_timertime = tr_timertime;
    }

    public int getCd_timertime() {
        return cd_timertime;
    }

    public void setCd_timertime(int cd_timertime) {
        this.cd_timertime = cd_timertime;
    }

    public void startStopwatch() throws IOException {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.cancelTask(trainingTask);
        if (scheduler.getPendingTasks().size() > 0) {
            scheduler.cancelTasks(main);
        }

        int stopwatchTask = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                seconds++;
                if (minutes < 60) {
                    if (seconds <= 59) {
                        if (seconds <= 9) {
                            if (minutes <= 9) {
                                formattedTime = String.format("0%s:0%s", minutes, seconds);
                            } else {
                                formattedTime = String.format("%s:0%s", minutes, seconds);
                            }
                        } else {
                            if (minutes <= 9) {
                                formattedTime = String.format("0%s:%s", minutes, seconds);
                            } else {
                                formattedTime = String.format("%s:%s", minutes, seconds);
                            }
                        }
                    } else {
                        if (seconds == 60) {
                            minutes++;
                            seconds = 0;
                            if (seconds <= 9) {
                                if (minutes <= 9) {
                                    formattedTime = String.format("0%s:0%s", minutes, seconds);
                                } else {
                                    formattedTime = String.format("%s:0%s", minutes, seconds);
                                }
                            } else {
                                if (minutes <= 9) {
                                    formattedTime = String.format("0%s:%s", minutes, seconds);
                                } else {
                                    formattedTime = String.format("%s:%s", minutes, seconds);
                                }
                            }
                        }
                    }
                } else if (minutes >= 60) {
                    hours++;
                    minutes = 0;
                    if (hours <= 9) {
                        if (seconds <= 59) {
                            if (seconds <= 9) {
                                if (minutes <= 9) {
                                    formattedTime = String.format("0%s:0%s:0%s", hours, minutes, seconds);
                                } else {
                                    formattedTime = String.format("0%s:%s:0%s", hours, minutes, seconds);
                                }
                            } else {
                                if (minutes <= 9) {
                                    formattedTime = String.format("0%s:0%s:%s", hours, minutes, seconds);
                                } else {
                                    formattedTime = String.format("0%s:%s:%s", hours, minutes, seconds);
                                }
                            }
                        } else {
                            if (seconds == 60) {
                                minutes++;
                                seconds = 0;
                                if (seconds <= 9) {
                                    if (minutes <= 9) {
                                        formattedTime = String.format("0%s:0%s:0%s", hours, minutes, seconds);
                                    } else {
                                        formattedTime = String.format("0%s:%s:0%s", hours, minutes, seconds);
                                    }
                                } else {
                                    if (minutes <= 9) {
                                        formattedTime = String.format("0%s:0%s:%s", hours, minutes, seconds);
                                    } else {
                                        formattedTime = String.format("0%s:%s:%s", hours, minutes, seconds);
                                    }
                                }
                            }
                        }
                    } else if (hours > 9) {
                        if (seconds <= 59) {
                            if (seconds <= 9) {
                                if (minutes <= 9) {
                                    formattedTime = String.format("%s:0%s:0%s", hours, minutes, seconds);
                                } else {
                                    formattedTime = String.format("%s:%s:0%s", hours, minutes, seconds);
                                }
                            } else {
                                if (minutes <= 9) {
                                    formattedTime = String.format("%s:0%s:%s", hours, minutes, seconds);
                                } else {
                                    formattedTime = String.format("%s:%s:%s", hours, minutes, seconds);
                                }
                            }
                        } else {
                            if (seconds == 60) {
                                minutes++;
                                seconds = 0;
                                if (seconds <= 9) {
                                    if (minutes <= 9) {
                                        formattedTime = String.format("0%s:0%s", minutes, seconds);
                                    } else {
                                        formattedTime = String.format("%s:0%s", minutes, seconds);
                                    }
                                } else {
                                    if (minutes <= 9) {
                                        formattedTime = String.format("0%s:%s", minutes, seconds);
                                    } else {
                                        formattedTime = String.format("%s:%s", minutes, seconds);
                                    }
                                }
                            }
                        }
                    }
                }

                main.games.set("latestGameDuration", seconds);
                main.games.set("formattedDuration", formattedTime);
                main.getLogger().info(String.valueOf(main.getServer().getScheduler().getPendingTasks().toArray().length));
                try {
                    main.games.save(main.gameConfigfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    checkState();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (String entry : game_board.getEntries()) {
                    if (entry.contains(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.time")))) {
                        game_board.resetScores(entry);
                        break;
                    }
                }
                game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.shootings")) + ChatColor.YELLOW + main.games.getString(id + ".shootings")).setScore(0);
                game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.players-ingame")) + ChatColor.YELLOW + main.games.getString(id + ".players")).setScore(-1);
                game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.players-left")) + ChatColor.YELLOW + main.games.getInt(id + ".playersLeft")).setScore(-2);
                game_board.getObjective("time").getScore(ChatColor.translateAlternateColorCodes('&', main.localization.getString(main.config.getString("lang") + ".scoreboard.time")) + ChatColor.YELLOW + formattedTime).setScore(-3);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    game_board.resetScores(p.getName());
                }
                game_board.getObjective("time").setDisplaySlot(DisplaySlot.SIDEBAR);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (main.games.contains(id + ".exclude") && !main.games.getList(id + ".exclude").contains(p.getDisplayName())) {
                        if (main.games.getString("latestFinishTime") == null) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b" + main.games.getString("formattedDuration"))));
                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a" + main.games.getString("formattedDuration") + " &b<<<----->>>&e " + main.games.getString("latestFinishTime"))));
                        }
                    }
                }
            }
        }, 0, 20L);
        main.log(String.format("Game %s: Stopwatch started.", id));
    }
}
