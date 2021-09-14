/*
 * KillTracker
 * Copyright (C) 2021 fisher2911
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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

import java.util.HashMap;
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
