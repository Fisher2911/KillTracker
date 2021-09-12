/*
 * MIT License
 *
 * Copyright (c) fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.fisher2911.killtracker;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.fisher2911.killtracker.command.KillTrackerCommand;
import me.fisher2911.killtracker.config.GuiSettings;
import me.fisher2911.killtracker.config.Settings;
import me.fisher2911.killtracker.database.Database;
import me.fisher2911.killtracker.database.SQLiteDatabase;
import me.fisher2911.killtracker.gui.StatsMenu;
import me.fisher2911.killtracker.listeners.KillListener;
import me.fisher2911.killtracker.listeners.PlayerJoinListener;
import me.fisher2911.killtracker.placeholderapi.KillTrackerExpansion;
import me.fisher2911.killtracker.user.User;
import me.fisher2911.killtracker.user.UserManager;
import me.mattstudios.mf.base.CommandManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

public class KillTracker extends JavaPlugin {

    private Settings settings;
    private GuiSettings guiSettings;
    private UserManager userManager;
    private Database database;
    private CommandManager commandManager;
    private BukkitTask saveTask;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public void onEnable() {
        int pluginId = 12761;
        Metrics metrics = new Metrics(this, pluginId);
        load();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            database.loadUser(player.getUniqueId()).
                    ifPresent(userManager::addUser);
        }
        registerExpansions();
    }

    @Override
    public void onDisable() {
        this.saveTask.cancel();
        saveAll();
        database.close();
    }

    private void load() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        this.settings = new Settings(this);
        this.guiSettings = new GuiSettings(this);
        this.userManager = new UserManager(this);
        this.database = new SQLiteDatabase(this);
        this.settings.load();
        this.guiSettings.load();
        this.registerListeners();
        this.registerCommands();
        startSaveTask();
    }

    private void startSaveTask() {
        final int saveInterval = Math.max(5, settings.getSaveInterval());
        this.saveTask = Bukkit.
                getScheduler().
                runTaskTimerAsynchronously(this,
                        this::saveAll, saveInterval, saveInterval );
    }

    private void saveAll() {
        userManager.getUserMap().values().forEach(database::saveUser);
    }

    private void registerCommands() {
        this.commandManager = new CommandManager(this);
        this.commandManager.register(new KillTrackerCommand(this));
    }

    private void registerListeners() {
        List.of(new KillListener(this),
                        new PlayerJoinListener(this)).
                forEach(listener -> this.getServer().
                        getPluginManager().
                        registerEvents(listener, this));
    }

    private void registerExpansions() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KillTrackerExpansion(this).register();
        }
    }

    public void sendError(final String error) {
        this.getLogger().severe(error);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Database getDatabase() {
        return this.database;
    }

    public StatsMenu getStatsMenu() {
        return guiSettings.getStatsMenu();
    }

    public void debug(final String message) {
        debug(message, settings.sendDebugMessages());
    }

    public void debug(final String message, final boolean send) {
        if (send && settings.sendDebugMessages()) {
            this.getLogger().warning("[DEBUG]: " + message);
        }
    }
}
